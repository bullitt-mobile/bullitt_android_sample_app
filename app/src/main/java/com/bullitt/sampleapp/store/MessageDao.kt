package com.bullitt.sampleapp.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(message: Message)

  @Update suspend fun update(message: Message)

  @Query("DELETE FROM message") suspend fun clear()

  @Query("SELECT * FROM message where partnerNumber = :partnerNumber ORDER BY timestamp ASC")
  fun getAllMessages(partnerNumber: Long): Flow<List<Message>>
}
