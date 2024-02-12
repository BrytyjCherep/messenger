package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChangeNameBinding
import com.example.myapplication.database.USER
import com.example.myapplication.database.setNameToDatabase
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
            setNameToDatabase(fullname)
        }
    }


}