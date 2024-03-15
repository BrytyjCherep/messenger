package com.example.myapplication.ui.message_recycler_view.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.ui.message_recycler_view.views.MessageView
import com.example.myapplication.utilits.AppVoicePlayer
import com.example.myapplication.utilits.asTime

class HolderVoiceMessage(view:View):RecyclerView.ViewHolder(view), MessageHolder {

    private val mAppVoicePlayer = AppVoicePlayer()

    private val blockReceivedVoiceMessage: ConstraintLayout = view.findViewById(R.id.block_received_voice_message)
    private val blockUserVoiceMessage: ConstraintLayout = view.findViewById(R.id.block_user_voice_message)
    private val chatUserVoiceMessageTime: TextView = view.findViewById(R.id.chat_user_voice_message_time)
    private val chatReceivedVoiceMessageTime: TextView = view.findViewById(R.id.chat_received_voice_message_time)

    private val chatReceivedBtnPlay:ImageView = view.findViewById(R.id.chat_received_btn_play)
    private val chatReceivedBtnStop:ImageView = view.findViewById(R.id.chat_received_btn_stop)

    private val chatUserBtnPlay:ImageView = view.findViewById(R.id.chat_user_btn_play)
    private val chatUserBtnStop:ImageView = view.findViewById(R.id.chat_user_btn_stop)

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockReceivedVoiceMessage.visibility = View.GONE
            blockUserVoiceMessage.visibility = View.VISIBLE
            chatUserVoiceMessageTime.text = view.timeStamp.asTime()
        } else {
            blockReceivedVoiceMessage.visibility = View.VISIBLE
            blockUserVoiceMessage.visibility = View.GONE
            chatReceivedVoiceMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        mAppVoicePlayer.init()
        if (view.from == CURRENT_UID){
            chatUserBtnPlay.setOnClickListener {
                chatUserBtnPlay.visibility = View.GONE
                chatUserBtnStop.visibility = View.VISIBLE
                chatUserBtnStop.setOnClickListener {
                    stop{
                        chatUserBtnStop.setOnClickListener(null)
                        chatUserBtnPlay.visibility = View.VISIBLE
                        chatUserBtnStop.visibility = View.GONE
                    }
                }
                play(view){
                    chatUserBtnPlay.visibility = View.VISIBLE
                    chatUserBtnStop.visibility = View.GONE
                }
            }
        } else {
            chatReceivedBtnPlay.setOnClickListener{
                chatReceivedBtnPlay.visibility = View.GONE
                chatReceivedBtnStop.visibility = View.VISIBLE
                chatReceivedBtnStop.setOnClickListener {
                    stop{
                        chatReceivedBtnStop.setOnClickListener(null)
                        chatReceivedBtnPlay.visibility = View.VISIBLE
                        chatReceivedBtnStop.visibility = View.GONE
                    }
                }
                play(view){
                    chatReceivedBtnPlay.visibility = View.VISIBLE
                    chatReceivedBtnStop.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        mAppVoicePlayer.play(view.id, view.fileUrl){
            function()
        }
    }

    fun stop(function: () -> Unit){
        mAppVoicePlayer.stop{
            function()
        }
    }

    override fun onDettach() {
        chatUserBtnPlay.setOnClickListener(null)
        chatReceivedBtnPlay.setOnClickListener(null)
        mAppVoicePlayer.release()
    }
}