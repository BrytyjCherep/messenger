package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.example.myapplication.activities.RegisterActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.models.User
import com.example.myapplication.ui.fragments.ChatsFragment
import com.example.myapplication.ui.objects.AppDrawer
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.AUTH
import com.example.myapplication.utilits.AppStates
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.utilits.CHILD_PHOTO_URL
import com.example.myapplication.utilits.NODE_USERS
import com.example.myapplication.utilits.REF_DATABASE_ROOT
import com.example.myapplication.utilits.CURRENT_UID
import com.example.myapplication.utilits.FOLDER_PROFILE_IMAGE
import com.example.myapplication.utilits.REF_STORAGE_ROOT
import com.example.myapplication.utilits.USER
import com.example.myapplication.utilits.initFirebase
import com.example.myapplication.utilits.initUser
import com.example.myapplication.utilits.replaceActivity
import com.example.myapplication.utilits.replaceFragment
import com.example.myapplication.utilits.showToast
import com.theartofdev.edmodo.cropper.CropImage

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    lateinit var mAppDrawer: AppDrawer
    private lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser{
            initFields()
            initFunc()
        }
    }


    private fun initFields() {
        mToolbar = mBinding.mainToolbar
        mAppDrawer = AppDrawer(this, mToolbar)

    }



    private fun initFunc() {
        //Firebase.database.getReference("message").setValue("hi")
        if (AUTH.currentUser!=null) {
            setSupportActionBar(mToolbar)
            mAppDrawer.create()
            replaceFragment(ChatsFragment(), false)
        } else {
            replaceActivity(RegisterActivity())
        }
    }

    fun gfiuds(){

    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)

    }

}