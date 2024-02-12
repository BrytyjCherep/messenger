package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChangeUsernameBinding
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.database.CHILD_USERNAME
import com.example.myapplication.database.NODE_USERNAMES
import com.example.myapplication.database.NODE_USERS
import com.example.myapplication.database.REF_DATABASE_ROOT
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.database.USER
import com.example.myapplication.database.updateCurrentUsername
import com.example.myapplication.utilits.showToast
import java.util.Locale

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    private var _binding: FragmentChangeUsernameBinding? = null
    private val binding get() = _binding!!

    lateinit var mNewUsername:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeUsernameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputUsername.setText(USER.username)
    }



    override fun change() {
        mNewUsername = binding.settingsInputUsername.text.toString().toLowerCase(Locale.getDefault())
        if (mNewUsername.isEmpty()){
            showToast("Поле пустое")
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener{
                    if (it.hasChild(mNewUsername)){
                        showToast("Такой пользователь уже существует")
                    } else {
                        changeUsername()
                    }
                })
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    updateCurrentUsername(mNewUsername)
                }
            }
    }





}