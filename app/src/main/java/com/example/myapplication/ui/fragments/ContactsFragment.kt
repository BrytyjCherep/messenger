package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.provider.ContactsContract.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ContactItemBinding
import com.example.myapplication.databinding.FragmentChangeBioBinding
import com.example.myapplication.databinding.FragmentContactsBinding
import com.example.myapplication.models.CommonModel
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.utilits.CURRENT_UID
import com.example.myapplication.utilits.CustomAdapter
import com.example.myapplication.utilits.NODE_PHONES_CONTACTS
import com.example.myapplication.utilits.NODE_USERS
import com.example.myapplication.utilits.REF_DATABASE_ROOT
import com.example.myapplication.utilits.downloadAndSetImage
import com.example.myapplication.utilits.getCommonModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import de.hdodenhof.circleimageview.CircleImageView

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CustomAdapter
    private lateinit var mRefContacts: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
        initRecyclerview()
    }

    private fun initRecyclerview() {
        mRecyclerView = binding.contactsRecycleView

        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)

        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java)
            .build()
        mAdapter = CustomAdapter(options)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter.startListening()
    }


    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        mAdapter.stopListener()
    }
}

