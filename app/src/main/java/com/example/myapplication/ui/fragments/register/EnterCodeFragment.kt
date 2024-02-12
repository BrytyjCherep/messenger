package com.example.myapplication.ui.fragments.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentEnterCodeBinding
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.database.AUTH
import com.example.myapplication.utilits.AppTextWatcher
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.database.CHILD_FULLNAME
import com.example.myapplication.database.CHILD_ID
import com.example.myapplication.database.CHILD_PHONE
import com.example.myapplication.database.CHILD_USERNAME
import com.example.myapplication.database.NODE_PHONES
import com.example.myapplication.database.NODE_USERNAMES
import com.example.myapplication.database.NODE_USERS
import com.example.myapplication.database.REF_DATABASE_ROOT
import com.example.myapplication.utilits.restartActivity
import com.example.myapplication.utilits.showToast
import com.google.firebase.auth.PhoneAuthProvider


class EnterCodeFragment(val phoneNumber: String, val id: String) : Fragment(R.layout.fragment_enter_code) {

    private var _binding: FragmentEnterCodeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnterCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.title = phoneNumber
        binding.registerInputCode.addTextChangedListener(AppTextWatcher {
            val string = binding.registerInputCode.text.toString()
            if (string.length >= 6) {
                enterCode()
            }

        })
    }

    private fun enterCode() {
        val code = binding.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber

                REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                    .addOnFailureListener{ showToast(it.message.toString()) }
                    .addOnSuccessListener {
                        REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                            .addListenerForSingleValueEvent(AppValueEventListener{
                                if (it.hasChild(CHILD_USERNAME)) {
                                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                                        .addOnSuccessListener {
                                            showToast("Добро пожаловать")
                                            restartActivity()
                                        }
                                        .addOnFailureListener{showToast(it.message.toString())}

                                }
                                else {
                                    dateMap[CHILD_FULLNAME] = "Пользователь"
                                    dateMap[CHILD_USERNAME] = uid
                                    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(uid).setValue(uid)
                                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                                        .addOnSuccessListener {
                                            showToast("Добро пожаловать")
                                            restartActivity()
                                        }
                                        .addOnFailureListener{showToast(it.message.toString())}
                                }

                            })
                    }
            } else {
                showToast(task.exception?.message.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}