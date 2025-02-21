package com.bullitt.sampleapp.messaging

import android.util.Log
import com.bullitt.sampleapp.store.Message
import com.bullitt.sampleapp.store.MessageDataSource
import com.bullitt.sampleapp.store.toMessage
import com.bullitt.sdk.platform.BullittApis
import com.bullitt.sdk.platform.data.Response
import com.bullitt.sdk.platform.data.device.BullittDeviceStatus
import com.bullitt.sdk.platform.data.events.GlobalEvent
import com.bullitt.sdk.platform.data.events.MessageEvent
import com.bullitt.sdk.platform.data.smp.content.SmpContent
import com.bullitt.sdk.platform.data.smp.generateMessageId
import com.bullitt.sdk.platform.device.SatDeviceConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SatMessagingClient
@Inject
constructor(
  private val bullittApis: BullittApis,
  private val messageDataSource: MessageDataSource,
) {
  private val coroutineScope = CoroutineScope(Dispatchers.Main)

  private var linkedDevice: SatDeviceConnection? = null

  companion object {
    private const val TAG = "SatMessagingClient"
  }

  init {
    coroutineScope.launch {
      bullittApis.globalEvents.collect {
        when (it) {
          is GlobalEvent.DeviceLinked -> setLinkedDevice(it.satDeviceConnection)
          is GlobalEvent.DeviceUpdate -> setLinkedDevice(it.deviceConnection)
          is GlobalEvent.DeviceUnlinked -> setLinkedDevice(null)
          is GlobalEvent.Message -> handleMessageEvent(it.messageEvent)
          else -> Unit
        }
      }
    }

    coroutineScope.launch {
      when (val response = bullittApis.getLinkedDevice()) {
        is Response.Failure -> {
          Log.d(TAG, "Failed to get linked device. Error: ${response.exception}")
        }

        is Response.Success -> {
          when (response.data.getStatus()) {
            is BullittDeviceStatus.Ble,
            is BullittDeviceStatus.D2d -> {
              setLinkedDevice(response.data)
            }
            else -> Log.d(TAG, "Sat device is not linked")
          }
        }
      }
    }
  }

  private fun setLinkedDevice(ld: SatDeviceConnection?) {
    Log.d(TAG, "Sat device changed: $ld")
    linkedDevice = ld
  }

  suspend fun sendMessage(content: SmpContent): Boolean {
    Log.d(TAG, "Sending message with content: $content")
    linkedDevice?.let { connection ->
      val contentBundle = connection.createContentBundle(content)
      insertMessage(contentBundle.toMessage(isOutgoing = true))
      when (val messageStatus = connection.sendMessage(contentBundle).await()) {
        is Response.Failure -> {
          Log.e(TAG, "Failed to send message. Error: ${messageStatus.exception}")
          updateMessageStatus(
            contentBundle.smpHeader.generateMessageId().toString(),
            Message.Status.FAILED,
          )
          return false
        }

        is Response.Success -> {
          val sentStatus = messageStatus.data.result
          val bytesUsed = messageStatus.data.bytesConsumed

          Log.d(TAG, "Send message returned success. Status: $sentStatus, Bytes used: $bytesUsed")

          updateMessageStatus(
            contentBundle.smpHeader.generateMessageId().toString(),
            if (sentStatus) Message.Status.SENT else Message.Status.FAILED,
          )
          return sentStatus
        }
      }
    }
      ?: run {
        Log.e(TAG, "Device not linked")
        return false
      }
  }

  private fun insertMessage(message: Message) {
    coroutineScope.launch(Dispatchers.IO) { messageDataSource.insertMessage(message) }
  }

  private fun updateMessageStatus(messageId: String, status: Message.Status) {
    coroutineScope.launch(Dispatchers.IO) {
      messageDataSource.updateMessageStatus(messageId, status)
    }
  }

  private fun handleMessageEvent(message: MessageEvent) {
    if (message is MessageEvent.MessageBundleReceived) {
      handleReceivedMessage(message)
    }
  }

  private fun handleReceivedMessage(messageEvent: MessageEvent.MessageBundleReceived) {
    val contentBundle = messageEvent.contentBundle
    val message = contentBundle.toMessage(isOutgoing = false)
    insertMessage(message)
  }
}
