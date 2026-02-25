package com.bullitt.sampleapp.store

import com.bullitt.sampleapp.messaging.decodeTextMessageV2
import com.bullitt.sampleapp.messaging.isTextMessageV2
import com.bullitt.sdk.platform.data.smp.ContentBundle
import com.bullitt.sdk.platform.data.smp.content.ExperimentalContent
import com.bullitt.sdk.platform.data.smp.content.TextContent
import com.bullitt.sdk.platform.data.smp.generateMessageId

/** Convert a ContentBundle to a Message, for incoming messages */
fun ContentBundle.toMessage(isOutgoing: Boolean): Message {
  val content =
    this.content.let {
      if (it is ExperimentalContent && it.isTextMessageV2()) it.decodeTextMessageV2() else it
    }

  return when (content) {
    is TextContent ->
      Message(
        partnerNumber = content.partnerId,
        content = content.textMessage,
        isOutgoing = isOutgoing,
        timestamp = this.smpHeader.eventTimestamp.timestampInMillis,
        status = if (isOutgoing) Message.Status.SENDING else Message.Status.DELIVERED,
        messageId = this.smpHeader.generateMessageId().toString(),
      )
    // We don't care about other message types in this sample application, but this can be handled
    // here
    else ->
      Message(
        partnerNumber = -1,
        content = content.contentType.name,
        isOutgoing = isOutgoing,
        timestamp = this.smpHeader.eventTimestamp.timestampInMillis,
        status = if (isOutgoing) Message.Status.SENDING else Message.Status.DELIVERED,
        messageId = this.smpHeader.generateMessageId().toString(),
      )
  }
}
