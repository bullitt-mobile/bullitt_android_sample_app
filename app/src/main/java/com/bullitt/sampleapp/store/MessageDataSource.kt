package com.bullitt.sampleapp.store

import android.app.Application
import androidx.room.Room
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

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

  suspend fun insertMessage(message: Message) {
    messageDao.insert(message)
  }

  suspend fun updateMessage(message: Message) {
    messageDao.update(message)
  }

  suspend fun clearMessages() {
    messageDao.clear()
  }

  fun getAllMessages(partnerNumber: Long): Flow<List<Message>> {
    return messageDao.getAllMessages(partnerNumber)
  }
}
