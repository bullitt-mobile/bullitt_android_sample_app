package com.bullitt.sampleapp.state

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bullitt.sampleapp.messaging.SatMessagingClient
import com.bullitt.sampleapp.store.MessageDataSource
import com.bullitt.sdk.platform.data.smp.SmpRequest
import com.bullitt.sdk.platform.data.smp.content.TextContent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
@Inject
constructor(
  private val messageDataSource: MessageDataSource,
  private val satMessagingClient: SatMessagingClient,
) : ViewModel() {
  fun getMessages(partnerNumber: Long) = messageDataSource.getAllMessages(partnerNumber)

  suspend fun sendMessage(message: String, partnerNumber: Long) {
    val textContent =
      TextContent(
        controlFlag = SmpRequest.Content.ControlFlag.DELIVERY_RECEIPT_REQUIRED,
        partnerId = partnerNumber,
        textMessage = message,
      )
    if (satMessagingClient.sendMessage(textContent)) {
      Log.d("ChatViewModel", "Message sent successfully")
    } else {
      Log.e("ChatViewModel", "Failed to send message")
    }
  }
}
