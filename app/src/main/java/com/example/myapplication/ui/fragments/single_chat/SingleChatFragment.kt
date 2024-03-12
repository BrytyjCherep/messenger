package com.example.myapplication.ui.fragments.single_chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.myapplication.database.FOLDER_MESSAGE_IMAGE
import com.example.myapplication.database.NODE_MESSAGES
import com.example.myapplication.database.NODE_USERS
import com.example.myapplication.database.REF_DATABASE_ROOT
import com.example.myapplication.database.REF_STORAGE_ROOT
import com.example.myapplication.database.TYPE_TEXT
import com.example.myapplication.utilits.downloadAndSetImage
import com.example.myapplication.database.getCommonModel
import com.example.myapplication.database.getMessageKey
import com.example.myapplication.database.getUrlFromStorage
import com.example.myapplication.database.getUserModel
import com.example.myapplication.database.putImageToStorage
import com.example.myapplication.database.sendMessage
import com.example.myapplication.database.sendMessageAsImage
import com.example.myapplication.database.uploadFileToStorage
import com.example.myapplication.utilits.AppChildEventListener
import com.example.myapplication.utilits.AppTextWatcher
import com.example.myapplication.utilits.AppVoiceRecorder
import com.example.myapplication.utilits.RECORD_AUDIO
import com.example.myapplication.utilits.checkPermissions
import com.example.myapplication.utilits.showToast
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    private var mCountMessages = 15
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder


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
        initFields()
        initToolbar()
        initRecycleView()
    }

    private fun initFields() {
        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = binding.chatSwipeRefresh
        mLayoutManager = LinearLayoutManager(this.context)
        binding.chatInputMessage.addTextChangedListener(AppTextWatcher{
            val string = binding.chatInputMessage.text.toString()
            if(string.isEmpty() || string == "Запись"){
                binding.chatBtnSendMessage.visibility = View.GONE
                binding.chatBtnAttach.visibility = View.VISIBLE
                binding.chatBtnVoice.visibility = View.VISIBLE
            } else {
                binding.chatBtnSendMessage.visibility = View.VISIBLE
                binding.chatBtnAttach.visibility = View.GONE
                binding.chatBtnVoice.visibility = View.GONE
            }
        })

        binding.chatBtnAttach.setOnClickListener {
            attachFile()
        }

        CoroutineScope(Dispatchers.IO).launch {
            binding.chatBtnVoice.setOnTouchListener { view, motionEvent ->
                if (checkPermissions(RECORD_AUDIO)){
                    if (motionEvent.action == MotionEvent.ACTION_DOWN){
                        binding.chatInputMessage.setText("Запись")
                        binding.chatBtnVoice.setColorFilter(ContextCompat.getColor(APP_ACTIVITY, com.mikepenz.materialize.R.color.primary))
                        val messageKey = getMessageKey(contact.id)
                        mAppVoiceRecorder.startRecord(messageKey)
                    } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                        binding.chatInputMessage.setText("")
                        binding.chatBtnVoice.colorFilter = null
                        mAppVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(Uri.fromFile(file), messageKey)
                        }
                    }
                }
                true
            }
        }
    }



    private fun attachFile() {
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(250,250)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecycleView() {
        mRecyclerView = binding.chatRecycleView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager
        mMessagesListener = AppChildEventListener{

            val message = it.getCommonModel()

            if (mSmoothScrollToPosition){
                mAdapter.addItemToBottom(message){
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(message){
                    mSwipeRefreshLayout.isRefreshing = false
                }
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
                println(mRecyclerView.recycledViewPool.getRecycledViewCount(0))
                if (mIsScrolling && dy < 0 && mLayoutManager.findLastVisibleItemPosition() <= 3){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val messageKey = getMessageKey(contact.id)

            val path = REF_STORAGE_ROOT
                .child(FOLDER_MESSAGE_IMAGE)
                .child(messageKey)

            putImageToStorage(uri, path){
                getUrlFromStorage(path){
                    sendMessageAsImage(contact.id, it, messageKey)
                    mSmoothScrollToPosition = true
                }
            }
        }
    }




    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
    }
}