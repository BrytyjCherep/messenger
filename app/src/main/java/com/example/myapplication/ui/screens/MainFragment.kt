package com.example.myapplication.ui.screens

import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.hideKeyboard

class MainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegram"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
    }

}