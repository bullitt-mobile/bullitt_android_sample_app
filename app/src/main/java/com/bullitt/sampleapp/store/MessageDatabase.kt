package com.bullitt.sampleapp.store

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Message::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MessageDatabase : RoomDatabase() {
  abstract fun messageDao(): MessageDao
}
