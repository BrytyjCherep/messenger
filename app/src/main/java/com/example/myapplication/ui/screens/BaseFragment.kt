package com.example.myapplication.ui.screens

import androidx.fragment.app.Fragment
import com.example.myapplication.utilits.APP_ACTIVITY


open class BaseFragment(layout:Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        (APP_ACTIVITY).mAppDrawer.disableDrawer()
    }
}