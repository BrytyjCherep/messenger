package com.example.myapplication.ui.fragments.single_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSingleChatBinding
import com.example.myapplication.models.CommonModel
import com.example.myapplication.models.UserModel
import com.example.myapplication.ui.fragments.BaseFragment
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.database.NODE_MESSAGES
import com.example.myapplication.database.NODE_USERS
import com.example.myapplication.database.REF_DATABASE_ROOT
import com.example.myapplication.database.TYPE_TEXT
import com.example.myapplication.utilits.downloadAndSetImage
import com.example.myapplication.database.getCommonModel
import com.example.myapplication.database.getUserModel
import com.example.myapplication.database.sendMessage
import com.example.myapplication.utilits.AppChildEventListener
import com.example.myapplication.utilits.showToast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {
    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = 10
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout


    private var _binding: FragmentSingleChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSingleChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mSwipeRefreshLayout = binding.chatSwipeRefresh
        initToolbar()
        initRecycleView()
    }

    private fun initRecycleView() {
        mRecyclerView = binding.chatRecycleView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter

        mMessagesListener = AppChildEventListener{
            mAdapter.addItem(it.getCommonModel(), mSmoothScrollToPosition){
                if (mSmoothScrollToPosition){
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
                mSwipeRefreshLayout.isRefreshing = false
            }
        }

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)

        mRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    mIsScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0){
                    updateData()
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.findViewById<ConstraintLayout>(R.id.toolbar_info)
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)

        /*//Найти элемент (кнопку) по ее id
        var btn: Button
        btn = APP_ACTIVITY.findViewById(R.id.chat_btn_send_message)*/
        binding.chatBtnSendMessage.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = binding.chatInputMessage.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else {
                sendMessage(message, contact.id, TYPE_TEXT){
                    binding.chatInputMessage.setText("")
                }
            }
        }
    }


    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_fullname).text = contact.fullname
        } else {
            mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_fullname).text =
                mReceivingUser.fullname
        }
        mToolbarInfo.findViewById<CircleImageView>(R.id.toolbar_chat_image)
            .downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_status).text = mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }
}