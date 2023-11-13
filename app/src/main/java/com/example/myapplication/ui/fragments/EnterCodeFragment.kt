package com.example.myapplication.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.activities.RegisterActivity
import com.example.myapplication.databinding.FragmentEnterCodeBinding
import com.example.myapplication.utilits.AUTH
import com.example.myapplication.utilits.AppTextWatcher
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.utilits.CHILD_ID
import com.example.myapplication.utilits.CHILD_PHONE
import com.example.myapplication.utilits.CHILD_USERNAME
import com.example.myapplication.utilits.NODE_PHONES
import com.example.myapplication.utilits.NODE_USERS
import com.example.myapplication.utilits.REF_DATABASE_ROOT
import com.example.myapplication.utilits.replaceActivity
import com.example.myapplication.utilits.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.log


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

        (activity as RegisterActivity).title = phoneNumber
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
                                            (activity as RegisterActivity).replaceActivity(MainActivity())
                                        }
                                        .addOnFailureListener{showToast(it.message.toString())}

                                }
                                else {
                                    dateMap[CHILD_USERNAME] = uid
                                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                                        .addOnSuccessListener {
                                            showToast("Добро пожаловать")
                                            (activity as RegisterActivity).replaceActivity(MainActivity())
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