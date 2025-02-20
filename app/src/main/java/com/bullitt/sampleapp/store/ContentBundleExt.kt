package com.bullitt.sampleapp.store

import com.bullitt.sdk.platform.data.smp.ContentBundle
import com.bullitt.sdk.platform.data.smp.content.TextContent

fun ContentBundle.toMessage(isOutgoing: Boolean): Message {
  val content = this.content
  // Convert SmpContent to a Pair<partnerId, content>
  val value: Pair<Long, String>? =
      when (content) {
        is TextContent -> Pair(content.partnerId, content.textMessage)
        else -> Pair(-1, content.contentType.name)
      }

  return Message(
      partnerNumber = value?.first,
      content = value?.second ?: content.contentType.name,
      isOutgoing = isOutgoing,
  )
}
