package com.example.myapplication.ui.message_recycler_view.view_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.database.getFileFromStorage
import com.example.myapplication.ui.message_recycler_view.views.MessageView
import com.example.myapplication.utilits.WRITE_FILES
import com.example.myapplication.utilits.asTime
import com.example.myapplication.utilits.checkPermissions
import com.example.myapplication.utilits.showToast
import java.io.File
import java.lang.Exception

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val blockReceivedFileMessage: ConstraintLayout =
        view.findViewById(R.id.block_received_file_message)
    private val blockUserFileMessage: ConstraintLayout =
        view.findViewById(R.id.block_user_file_message)
    private val chatUserFileMessageTime: TextView =
        view.findViewById(R.id.chat_user_file_message_time)
    private val chatReceivedFileMessageTime: TextView =
        view.findViewById(R.id.chat_received_file_message_time)

    private val chatUserFileName: TextView = view.findViewById(R.id.chat_user_filename)
    private val chatUserBtnDownload: ImageView = view.findViewById(R.id.chat_user_btn_download)
    private val chatUserProgressBar: ProgressBar = view.findViewById(R.id.chat_user_progress_bar)

    private val chatReceivedFileName: TextView = view.findViewById(R.id.chat_received_filename)
    private val chatReceivedBtnDownload: ImageView =
        view.findViewById(R.id.chat_received_btn_download)
    private val chatReceivedProgressBar: ProgressBar =
        view.findViewById(R.id.chat_received_progress_bar)

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockReceivedFileMessage.visibility = View.GONE
            blockUserFileMessage.visibility = View.VISIBLE
            chatUserFileMessageTime.text = view.timeStamp.asTime()
            chatUserFileName.text = view.text
        } else {
            blockReceivedFileMessage.visibility = View.VISIBLE
            blockUserFileMessage.visibility = View.GONE
            chatReceivedFileMessageTime.text = view.timeStamp.asTime()
            chatReceivedFileName.text = view.text
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserBtnDownload.setOnClickListener { clickToBtnFile(view) }
        } else {
            chatReceivedBtnDownload.setOnClickListener { clickToBtnFile(view) }
        }
    }

    private fun clickToBtnFile(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserBtnDownload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else {
            chatReceivedBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )
        try {

            file.createNewFile()
            getFileFromStorage(file, view.fileUrl) {
                if (view.from == CURRENT_UID) {
                    chatUserBtnDownload.visibility = View.VISIBLE
                    chatUserProgressBar.visibility = View.INVISIBLE
                } else {
                    chatReceivedBtnDownload.visibility = View.VISIBLE
                    chatReceivedProgressBar.visibility = View.INVISIBLE
                }
            }

        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    override fun onDettach() {
        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)
    }
}