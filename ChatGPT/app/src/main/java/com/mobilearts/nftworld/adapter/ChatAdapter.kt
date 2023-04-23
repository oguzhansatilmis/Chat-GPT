package com.mobilearts.nftworld.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobilearts.nftworld.R
import com.mobilearts.nftworld.database.RoomEntity
import javax.inject.Inject

class ChatAdapter @Inject constructor():RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((RoomEntity) -> Unit)? = null
   var messageList: MutableList<RoomEntity> = mutableListOf()

    class UserMessageViewHolder(itemView : View) :RecyclerView.ViewHolder(itemView){
        val userMessageTV: TextView = itemView.findViewById(R.id.userMessageTV)
    }
    class BotMessageViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val botMessageTV :TextView = itemView.findViewById(R.id.bot_message_TV)
        val copyBtn: ImageView = itemView.findViewById(R.id.iconCopy)
    }
    class TypingViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val botTyping :TextView = itemView.findViewById(R.id.bot_typing_tv)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_message, parent, false)
                UserMessageViewHolder(view)
            }
            VIEW_TYPE_BOT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.bot_message, parent, false)
                BotMessageViewHolder(view)
            }
            VIEW_TYPE_TYPING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.bot_message_typing, parent, false)
                TypingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid ui type")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_USER -> (holder as UserMessageViewHolder).userMessageTV.text = messageList[position].message
            VIEW_TYPE_BOT -> {
                val botMessage = messageList[position]
                val botViewHolder = holder as BotMessageViewHolder
                botViewHolder.botMessageTV.text = botMessage.message
                botViewHolder.copyBtn.setOnClickListener { onItemClick?.invoke(messageList[position]) }
            }
            VIEW_TYPE_TYPING -> (holder as TypingViewHolder).botTyping.text = messageList[position].message
        }
    }
    override fun getItemCount(): Int {
        return messageList.size
    }
    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_BOT = 1
        private const val VIEW_TYPE_TYPING = 2
    }
    override fun getItemViewType(position: Int): Int {
        return when (messageList[position].sender) {
            "user" -> VIEW_TYPE_USER
            "bot" -> VIEW_TYPE_BOT
            "typing" -> VIEW_TYPE_TYPING
            else -> throw IllegalArgumentException("Invalid message type")
        }
    }



}