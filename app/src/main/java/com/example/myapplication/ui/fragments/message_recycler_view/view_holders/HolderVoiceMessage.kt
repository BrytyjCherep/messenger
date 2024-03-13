package com.example.myapplication.ui.fragments.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.ui.fragments.message_recycler_view.views.MessageView
import com.example.myapplication.utilits.asTime

class HolderVoiceMessage(view:View):RecyclerView.ViewHolder(view) {
    val blockReceivedVoiceMessage: ConstraintLayout = view.findViewById(R.id.block_received_voice_message)
    val blockUserVoiceMessage: ConstraintLayout = view.findViewById(R.id.block_user_voice_message)
    val chatUserVoiceMessageTime: TextView = view.findViewById(R.id.chat_user_voice_message_time)
    val chatReceivedVoiceMessageTime: TextView = view.findViewById(R.id.chat_received_voice_message_time)

    val chatReceivedBtnPlay:ImageView = view.findViewById(R.id.chat_received_btn_play)
    val chatReceivedBtnStop:ImageView = view.findViewById(R.id.chat_received_btn_stop)

    val chatUserBtnPlay:ImageView = view.findViewById(R.id.chat_user_btn_play)
    val chatUserBtnStop:ImageView = view.findViewById(R.id.chat_user_btn_stop)

    fun drawMessageVoice(holder: HolderVoiceMessage, view: MessageView) {
        if (view.from == CURRENT_UID) {
            holder.blockReceivedVoiceMessage.visibility = View.GONE
            holder.blockUserVoiceMessage.visibility = View.VISIBLE
            holder.chatUserVoiceMessageTime.text = view.timeStamp.asTime()
        } else {
            holder.blockReceivedVoiceMessage.visibility = View.VISIBLE
            holder.blockUserVoiceMessage.visibility = View.GONE
            holder.chatReceivedVoiceMessageTime.text = view.timeStamp.asTime()
        }
    }
}