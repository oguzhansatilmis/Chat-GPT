package com.mobilearts.nftworld.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoomEntity::class], version = 1, exportSchema = false)
abstract class MessageDatabase :RoomDatabase() {
    abstract fun messageDao() :MessageDao

    companion object{
        @Volatile
        private var INSTANCE :MessageDatabase? = null

        fun getDatabase(context: Context) :MessageDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessageDatabase::class.java,
                    "message_database"

                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}