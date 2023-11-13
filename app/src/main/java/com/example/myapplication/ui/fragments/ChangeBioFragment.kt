package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChangeBioBinding
import com.example.myapplication.utilits.CHILD_BIO
import com.example.myapplication.utilits.NODE_USERS
import com.example.myapplication.utilits.REF_DATABASE_ROOT
import com.example.myapplication.utilits.CURRENT_UID
import com.example.myapplication.utilits.USER
import com.example.myapplication.utilits.showToast

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    private var _binding: FragmentChangeBioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeBioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.settingsInputBio.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val newBio = binding.settingsInputBio.text.toString()
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(newBio)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    showToast(getString(R.string.toast_data_update))
                    USER.bio = newBio
                    parentFragmentManager.popBackStack()
                }
            }
    }


}