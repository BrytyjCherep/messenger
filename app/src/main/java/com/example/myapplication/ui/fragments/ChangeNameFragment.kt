package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChangeNameBinding
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.CHILD_FULLNAME
import com.example.myapplication.utilits.NODE_USERS
import com.example.myapplication.utilits.REF_DATABASE_ROOT
import com.example.myapplication.utilits.CURRENT_UID
import com.example.myapplication.utilits.USER
import com.example.myapplication.utilits.showToast

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {
    private var _binding: FragmentChangeNameBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initFullnameList()
    }

    private fun initFullnameList(){
        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1){
            binding.settingsInputName.setText(fullnameList[0])
            binding.settingsInputSurname.setText(fullnameList[2])
        } else {
            binding.settingsInputName.setText(fullnameList[0])
        }
    }

    override fun change() {
        val name: String = binding.settingsInputName.text.toString()
        val surname = binding.settingsInputSurname.text.toString()
        if (name.isEmpty()){
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            var fullname = "$name  $surname"
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
                .setValue(fullname).addOnCompleteListener {
                    if(it.isSuccessful){
                        showToast(getString(R.string.toast_data_update))
                        USER.fullname = fullname
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                        parentFragmentManager?.popBackStack()
                    }
                }
        }
    }
}