package com.example.myapplication.ui.fragments.message_recycler_view.view_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.ui.fragments.message_recycler_view.views.MessageView
import com.example.myapplication.utilits.asTime

class HolderTextMessage(view: View):RecyclerView.ViewHolder(view) {
    val blockUserMessage: ConstraintLayout = view.findViewById(R.id.block_user_message)
    val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
    val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)
    val blockReceivedMessage: ConstraintLayout = view.findViewById(R.id.block_received_message)
    val chatReceivedMessage: TextView = view.findViewById(R.id.chat_received_message)
    val chatReceivedMessageTime: TextView = view.findViewById(R.id.chat_received_message_time)

    fun drawMessageText(holder: HolderTextMessage, view: MessageView) {
        if (view.from == CURRENT_UID) {
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockReceivedMessage.visibility = View.GONE
            holder.chatUserMessage.text = view.text
            holder.chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            holder.blockUserMessage.visibility = View.GONE
            holder.blockReceivedMessage.visibility = View.VISIBLE
            holder.chatReceivedMessage.text = view.text
            holder.chatReceivedMessageTime.text =
                view.timeStamp.asTime()
        }
    }
}