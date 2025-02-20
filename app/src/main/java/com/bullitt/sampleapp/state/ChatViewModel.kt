package com.bullitt.sampleapp.state

import androidx.lifecycle.ViewModel
import com.bullitt.sampleapp.store.Message
import com.bullitt.sampleapp.store.MessageDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
  private val messageDataSource: MessageDataSource
): ViewModel() {
  fun getMessages(partnerNumber: Long) = messageDataSource.getAllMessages(partnerNumber)

  suspend fun sendMessage(message: Message) {
    messageDataSource.insertMessage(
      message.copy(
        status = Message.Status.SENDING,
      )
    )

    delay(1000)

    messageDataSource.updateMessage(
      message.copy(
        status = Message.Status.SENT,
      )
    )

    // TODO: Sending message logic. Mark sent when success, else mark failed
  }
}
