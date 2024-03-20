package com.example.myapplication.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.ui.message_recycler_view.views.MessageView
import com.example.myapplication.utilits.asTime

class HolderTextMessage(view: View):RecyclerView.ViewHolder(view), MessageHolder {
    private val blockUserMessage: ConstraintLayout = view.findViewById(R.id.block_user_message)
    private val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
    private  val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)
    private val blockReceivedMessage: ConstraintLayout = view.findViewById(R.id.block_received_message)
    private val chatReceivedMessage: TextView = view.findViewById(R.id.chat_received_message)
    private val chatReceivedMessageTime: TextView = view.findViewById(R.id.chat_received_message_time)
    private val chatUserMessageStatusUnchecked: ImageView = view.findViewById(R.id.chat_message_status_unchecked)
    private val chatUserMessageStatusChecked: ImageView = view.findViewById(R.id.chat_message_status_checked)

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text =
                view.timeStamp.asTime()
            if (view.status == "checked") {
                chatUserMessageStatusChecked.visibility = View.VISIBLE
                chatUserMessageStatusUnchecked.visibility = View.GONE
            } else {
                chatUserMessageStatusChecked.visibility = View.GONE
                chatUserMessageStatusUnchecked.visibility = View.VISIBLE
            }
        } else {
            blockUserMessage.visibility = View.GONE
            blockReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text =
                view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDettach() {
    }
}