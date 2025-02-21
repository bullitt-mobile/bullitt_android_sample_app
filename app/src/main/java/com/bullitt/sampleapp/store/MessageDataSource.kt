package com.bullitt.sampleapp.store

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageDataSource @Inject constructor(context: Application) {
  private val messageDao: MessageDao

  init {
    val database =
      Room.databaseBuilder(
          context.applicationContext,
          MessageDatabase::class.java,
          "message_database",
        )
        .build()
    messageDao = database.messageDao()
  }

  suspend fun insertMessage(message: Message) = messageDao.insert(message)

  suspend fun updateMessage(message: Message) = messageDao.update(message)

  suspend fun clearMessages() {
    messageDao.clear()
  }

  fun updateMessageStatus(messageId: String, status: Message.Status) {
    messageDao.updateMessageStatus(messageId, status)
  }

  fun getAllMessages(partnerNumber: Long): Flow<List<Message>> {
    return messageDao.getAllMessages(partnerNumber)
  }
}
