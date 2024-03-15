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
import com.example.myapplication.utilits.downloadAndSetImage

class HolderImageMessage(view:View):RecyclerView.ViewHolder(view), MessageHolder {
    private val blockReceivedImageMessage: ConstraintLayout = view.findViewById(R.id.block_received_image_message)
    private val blockUserImageMessage: ConstraintLayout = view.findViewById(R.id.block_user_image_message)
    private val chatUserImage: ImageView = view.findViewById(R.id.chat_user_image)
    private val chatReceivedImage: ImageView = view.findViewById(R.id.chat_received_image)
    private val chatUserImageMessageTime: TextView = view.findViewById(R.id.chat_user_image_message_time)
    private val chatReceivedImageMessageTime: TextView = view.findViewById(R.id.chat_received_image_message_time)

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockReceivedImageMessage.visibility = View.GONE
            blockUserImageMessage.visibility = View.VISIBLE
            chatUserImage.downloadAndSetImage(view.fileUrl)
            chatUserImageMessageTime.text = view.timeStamp.asTime()
        } else {
            blockReceivedImageMessage.visibility = View.VISIBLE
            blockUserImageMessage.visibility = View.GONE
            chatReceivedImage.downloadAndSetImage(view.fileUrl)
            chatReceivedImageMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDettach() {
    }
}