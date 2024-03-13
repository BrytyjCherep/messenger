package com.example.myapplication.ui.fragments.single_chat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.ui.fragments.message_recycler_view.view_holders.AppHolderFactory
import com.example.myapplication.ui.fragments.message_recycler_view.view_holders.HolderImageMessage
import com.example.myapplication.ui.fragments.message_recycler_view.view_holders.HolderTextMessage
import com.example.myapplication.ui.fragments.message_recycler_view.view_holders.HolderVoiceMessage
import com.example.myapplication.ui.fragments.message_recycler_view.views.MessageView
import com.example.myapplication.utilits.asTime
import com.example.myapplication.utilits.downloadAndSetImage

class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListMessagesCache = mutableListOf<MessageView>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = mListMessagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is HolderImageMessage -> holder.drawMessageImage(holder, mListMessagesCache[position])
            is HolderTextMessage -> holder.drawMessageText(holder, mListMessagesCache[position])
            is HolderVoiceMessage -> holder.drawMessageVoice(holder, mListMessagesCache[position])
            else -> {
            }
        }
    }

    fun addItemToTop(item: MessageView,
                        onSuccess:() -> Unit) {
        if (!mListMessagesCache.contains(item)){
            mListMessagesCache.add(item)
            mListMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun addItemToBottom(item: MessageView,
                        onSuccess:() -> Unit) {
        if (!mListMessagesCache.contains(item)){
            mListMessagesCache.add(item)
            notifyItemInserted(mListMessagesCache.size)
        }
        onSuccess()
    }

}


