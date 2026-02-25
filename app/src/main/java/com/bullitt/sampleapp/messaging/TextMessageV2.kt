/**
 * Copyright (c) 2026 RCD Bullitt Ltd.
 *
 * This is a custom type used by Bullitt to transmit text messages downlink in specific cases.
 *
 * To enable group messaging and merge location into one type compared to Skylo, this custom format
 * was created by us. It's only added here to be inbound as this may come through satellite if the
 * user is registered on our server and has used the recent versions of the Bullitt Satellite
 * Messenger.
 *
 * If the client app does not require using our backend server, this type is not required, but
 * otherwise, it needs to be added inbound like so
 */
package com.bullitt.sampleapp.messaging

import com.bullitt.sdk.platform.data.smp.SMPUtils
import com.bullitt.sdk.platform.data.smp.SmpRequest
import com.bullitt.sdk.platform.data.smp.content.ExperimentalContent
import com.bullitt.sdk.platform.data.smp.content.TextContent
import java.nio.ByteBuffer

const val TEXT_MESSAGE_V2_CONTENT_TYPE: Byte = 0x0B

fun ExperimentalContent.isTextMessageV2() = payload[0] == TEXT_MESSAGE_V2_CONTENT_TYPE

fun ExperimentalContent.decodeTextMessageV2(): TextContent {
  val buffer = ByteBuffer.wrap(payload)

  val contentOtherType = buffer.get()
  if (contentOtherType != 0x0B.toByte()) {
    throw IllegalArgumentException("Invalid contentOtherType for TextMessageV2: $contentOtherType")
  }

  val userIdBytes = ByteArray(6)
  buffer.get(userIdBytes)
  val userIdentifier = userIdBytes.decodeUserId()

  // Skip 14 bytes
  buffer.position(buffer.position() + 14)

  val textPayloadBytes = ByteArray(buffer.remaining())
  buffer.get(textPayloadBytes)
  val textPayload = String(textPayloadBytes)

  return TextContent(
    SmpRequest.Content.ControlFlag.READ_RECEIPT_REQUIRED,
    userIdentifier,
    textPayload,
  )
}

fun ByteArray.decodeUserId() = SMPUtils.convertByteArrayToHexString(this).toLong(16)
