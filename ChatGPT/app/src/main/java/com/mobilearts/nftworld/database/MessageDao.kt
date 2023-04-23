package com.mobilearts.nftworld.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface MessageDao {

    @Query("SELECT * FROM chat")
    fun getAllMessages(): Flow<List<RoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserMessage(messageEntity: RoomEntity)

   @Query("DELETE FROM chat WHERE sender = 'typing'")
    suspend fun deleteTypingMessages()

    @Query("DELETE FROM chat")
    suspend fun deleteAllMessages()


}