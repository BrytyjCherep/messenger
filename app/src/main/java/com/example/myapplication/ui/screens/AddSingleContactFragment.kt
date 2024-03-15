package com.example.myapplication.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAddSingleContactBinding
import com.example.myapplication.database.CHILD_ID
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.database.NODE_PHONES_CONTACTS
import com.example.myapplication.database.NODE_USERNAMES
import com.example.myapplication.database.REF_DATABASE_ROOT
import com.example.myapplication.utilits.showToast

class AddSingleContactFragment : BaseFragment(R.layout.fragment_add_single_contact) {
    private var _binding: FragmentAddSingleContactBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddSingleContactBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        binding.addSingleContactBtn.setOnClickListener {
            var contactname = binding.addSingleContactText.text.toString()
            if (contactname.isEmpty()){
                showToast("Имя пустое")
            }
            else {
                var uid: String = ""
                REF_DATABASE_ROOT.child(NODE_USERNAMES).child(contactname).get().addOnSuccessListener {
                    uid = it.value.toString()
                    if (uid == "null"){
                        showToast("Такого пользователя не существует")
                    }
                    else {
                        if (uid != CURRENT_UID) {
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                                .child(uid).child(CHILD_ID).setValue(uid)
                                .addOnSuccessListener {
                                    showToast("Пользователь добавлен")
                                }
                        } else {
                            showToast("Вы ввели собственное имя")
                        }
                    }
                }

            }
        }
    }
}