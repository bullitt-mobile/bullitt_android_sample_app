package com.bullitt.sampleapp.store

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val status: Status = Status.SENDING,
  val isOutgoing: Boolean,
  val partnerNumber: Long?,
  val content: String,
  val messageId: String,
  val timestamp: Long = System.currentTimeMillis(),
) {
  enum class Status(val value: Int) {
    SENDING(0),
    FAILED(1),
    SENT(2),
    DELIVERED(3),
    READ(4);

    companion object {
      fun fromInt(value: Int) = entries.first { it.value == value }
    }
  }
}
