package com.example.myapplication.ui.screens.main_list

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.database.NODE_MAIN_LIST
import com.example.myapplication.database.NODE_MESSAGES
import com.example.myapplication.database.NODE_USERS
import com.example.myapplication.database.REF_DATABASE_ROOT
import com.example.myapplication.database.getCommonModel
import com.example.myapplication.models.CommonModel
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.utilits.TYPE_MESSAGE_FILE
import com.example.myapplication.utilits.TYPE_MESSAGE_IMAGE
import com.example.myapplication.utilits.TYPE_MESSAGE_TEXT
import com.example.myapplication.utilits.TYPE_MESSAGE_VOICE
import com.example.myapplication.utilits.hideKeyboard

class MainListFragment : Fragment(R.layout.fragment_main_list) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    private var mListItems = listOf<CommonModel>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegram"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = APP_ACTIVITY.findViewById(R.id.main_list_recycle_view)
        mAdapter = MainListAdapter()

        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener{ dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach{model ->

                mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener{ dataSnapshot1 ->
                    val newModel = dataSnapshot1.getCommonModel()

                    mRefMessages.child(model.id).limitToLast(1).addListenerForSingleValueEvent(AppValueEventListener{ dataSnapshot2 ->
                        val tempList = dataSnapshot2.children.map { it.getCommonModel() }
                        if (tempList[0].type == TYPE_MESSAGE_IMAGE){
                            newModel.lastMessage = "Изображение"
                        } else if (tempList[0].type == TYPE_MESSAGE_VOICE){
                            newModel.lastMessage = "Голосовое сообщение"
                        } else if (tempList[0].type == TYPE_MESSAGE_FILE){
                            newModel.lastMessage = "Файл"
                        } else if (tempList[0].type == TYPE_MESSAGE_TEXT){
                            newModel.lastMessage = tempList[0].text
                        }

                        if(newModel.fullname.isEmpty()){
                            newModel.fullname = newModel.phone
                        }
                        mAdapter.updateListItems(newModel)
                    })
                })
            }
        })

        mRecyclerView.adapter = mAdapter
    }

}