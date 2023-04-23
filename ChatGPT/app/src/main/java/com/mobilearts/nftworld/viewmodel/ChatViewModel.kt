package com.mobilearts.nftworld.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilearts.nftworld.repository.Repository
import com.mobilearts.nftworld.database.RoomEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor( val repository: Repository) :ViewModel(){
    private val _liste = MutableStateFlow<List<RoomEntity>>(emptyList())
    val allMessageList: Flow<List<RoomEntity>> = repository.allMessages
    suspend fun getResponse(query: String){
        addMessage(RoomEntity(0,query,"user"))
        emitTyping()

        viewModelScope.launch {

            val chatModel = repository.getResponse(query)
            chatModel.collectLatest {
                repository.deleteaTypingMessages()
                addMessage(it)
            }
        }
    }
    private suspend fun emitTyping(){
        viewModelScope.launch {
            delay(100)
            addMessage(RoomEntity(0,"","typing"))
        }
    }

    suspend fun deleteMessage(){
        viewModelScope.launch {
            repository.deleteAllMessage()
        }
    }


    suspend fun botWelcomeMessage(){
        viewModelScope.launch {
            delay(100)
            addMessage(RoomEntity(0,"Hello, how can I help you ?","bot"))
        }
    }

    private suspend fun addMessage(roomEntity: RoomEntity){
        val messages = _liste.value.toMutableList()
        messages.add(roomEntity)
        repository.insertMessage(roomEntity)
        _liste.value=messages

    }
}