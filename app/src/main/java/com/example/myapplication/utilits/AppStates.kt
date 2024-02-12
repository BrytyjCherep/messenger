package com.example.myapplication.utilits

import com.example.myapplication.database.AUTH
import com.example.myapplication.database.CHILD_STATE
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.database.NODE_USERS
import com.example.myapplication.database.REF_DATABASE_ROOT
import com.example.myapplication.database.USER

enum class AppStates(val state:String) {

    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("печатает");

    companion object{
        fun updateState(appStates: AppStates){
            if (AUTH.currentUser!=null){
                REF_DATABASE_ROOT
                    .child(NODE_USERS)
                    .child(CURRENT_UID)
                    .child(CHILD_STATE)
                    .setValue(appStates.state)
                    .addOnSuccessListener { USER.state = appStates.state }
                    .addOnFailureListener{showToast(it.message.toString())}
            }
        }
    }
}