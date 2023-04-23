package com.mobilearts.nftworld.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobilearts.nftworld.adapter.ChatAdapter
import com.mobilearts.nftworld.viewmodel.ChatViewModel
import com.mobilearts.nftworld.R
import com.mobilearts.nftworld.database.RoomEntity
import com.mobilearts.nftworld.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding

    @Inject
     lateinit var chatRVAdapter: ChatAdapter
    private val messageList: MutableList<RoomEntity> = mutableListOf()
    private var lastMessage = ""
    private val viewModel: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextListener()

        fillMessageList()

        createRecyclerView()
        copyMessage()

        binding.sendButton.setOnClickListener {
            messageSending()
        }
        binding.textView3.setOnClickListener {
            viewModel.viewModelScope.launch {
                viewModel.repository.deleteAllMessage()
            }
        }

        binding.refreshBtn.setOnClickListener {

            if(lastMessage == ""){
                println("you haven't written a message")
            }
            else{
                sendLastMessage()
            }

        }
    }
    private fun editTextListener(){
        binding.apply {
            chatEditText.addTextChangedListener( object :TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
                @SuppressLint("SuspiciousIndentation")
                override fun afterTextChanged(p0: Editable?) {
                  if(p0.isNullOrEmpty()){
                      sendButton.setImageResource(R.drawable.icon_send_unselected)
                  }else{
                      sendButton.setImageResource(R.drawable.icon_send_selected)
                  }
                }

            })
        }
    }
    private fun messageSending(){
        binding.apply {
            val message = chatEditText.text.toString().trim()
            lastMessage = message
            println("gönderilen" +message)
            if(message.isNotEmpty()){
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getResponse(message)
                }
                chatEditText.text.clear()
                scrollToBottom()
              chatRecyler.smoothScrollToPosition(messageList.size-1)

            }else{
                Toast.makeText(
                    requireContext(),
                    "yazı gir",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun fillMessageList(){
        viewModel.viewModelScope.launch {
            viewModel.allMessageList.collectLatest { messages->
                println("collect" + messages)
            chatRVAdapter.messageList = messages.toMutableList() as ArrayList<RoomEntity>

            if(messages.isNotEmpty()){
                chatRVAdapter.notifyItemInserted(messages.size -1)
                scrollToBottom()
                binding.chatRecyler.scrollToPosition(messages.size-1)
            }else{
               viewModel.botWelcomeMessage()
            }

            }
        }
    }
    private fun scrollToBottom(){
        binding.apply {
            chatRecyler.addOnLayoutChangeListener{ _, _, _, _, bottom, _, _, _, oldBottom ->
                if(bottom <oldBottom){
                    chatRecyler.smoothScrollToPosition(messageList.size)
                }
            }
        }
    }
    private fun createRecyclerView() {
        binding.apply {

            chatRVAdapter = ChatAdapter()
            val layoutManager = LinearLayoutManager(context)
            chatRecyler.layoutManager = layoutManager
            chatRecyler.adapter = chatRVAdapter
            chatRecyler.scrollToPosition(messageList.size - 1)
            getAllData()
            scrollToBottom()
        }
    }
    private fun getAllData() {
        viewModel.viewModelScope.launch {
            viewModel.allMessageList.collectLatest {
                messageList.clear()
                messageList.addAll(it)
                println(messageList)
                chatRVAdapter.notifyDataSetChanged()
                Log.d("messagelist", "${messageList.size}")
            }
        }
        scrollToBottom()
    }
    private fun sendLastMessage(){
        viewModel.viewModelScope.launch {
            viewModel.getResponse(lastMessage)
        }
    }
    private fun copyMessage(){
        binding.apply {
            chatRVAdapter.onItemClick = { click ->
                copiedIV.visibility = View.VISIBLE

                val clipboardManager =
                    requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val text =click.message

                val clip = ClipData.newPlainText("text",text)
                clipboardManager.setPrimaryClip(clip)
                Handler(Looper.getMainLooper()).postDelayed({
                    copiedIV.visibility = View.GONE
                },1000)

            }
        }
    }

}

