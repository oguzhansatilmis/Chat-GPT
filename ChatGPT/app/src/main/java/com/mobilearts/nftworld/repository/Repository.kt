package com.mobilearts.nftworld.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.mobilearts.nftworld.data.ApiService
import com.mobilearts.nftworld.database.MessageDao
import com.mobilearts.nftworld.database.RoomEntity
import com.mobilearts.nftworld.model.RequestModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class Repository@Inject constructor(val apiService: ApiService, val imageDAO: MessageDao) {

    val allMessages: Flow<List<RoomEntity>> = imageDAO.getAllMessages()

    @WorkerThread
    suspend fun insertMessage(roomEntity: RoomEntity){
        imageDAO.inserMessage(roomEntity)
    }
    @WorkerThread
    suspend fun deleteaTypingMessages() {
        imageDAO.deleteTypingMessages()
    }
    @WorkerThread
    suspend fun deleteAllMessage() {
        imageDAO.deleteAllMessages()
    }

    suspend fun getResponse(query :String) :Flow<RoomEntity> = flow {
        val response = apiService.getResponse(
            RequestModel(
                model = "gpt-3.5-turbo-0301",
                messages = listOf(
                    RequestModel.Message(
                        role = "user",
                        content = query
                    )
                ),
                temperature = 0.1,
                max_tokens = 4000
            )
        )
        val responseMsg = response.choices[0].message.content
        println(responseMsg)
        val adjusted :String = responseMsg.replace("\n","")
        emit(RoomEntity(0,adjusted,"bot"))

    }.catch { e ->

        Log.e("ChatRepository","error $e")

        emit(RoomEntity(0,"error","bot"))
    }.flowOn(Dispatchers.IO)

}