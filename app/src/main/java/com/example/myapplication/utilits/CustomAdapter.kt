package com.example.myapplication.utilits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ContactItemBinding
import com.example.myapplication.models.CommonModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference


class CustomAdapter(options: FirebaseRecyclerOptions<CommonModel>) :
    FirebaseRecyclerAdapter<CommonModel, CustomAdapter.ContactsHolder>(options) {

    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder =
        ContactsHolder(
            ContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ContactsHolder, position: Int, model: CommonModel) {
        val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

        mRefUsersListener = AppValueEventListener {
            val contact = it.getCommonModel()
            holder.bind(contact)
        }

        mRefUsers.addValueEventListener(mRefUsersListener)
        mapListeners[mRefUsers] = mRefUsersListener
    }


    class ContactsHolder(_binding: ContactItemBinding) : RecyclerView.ViewHolder(_binding.root) {
        var binding = ContactItemBinding.bind(itemView)

        fun bind(commonModel: CommonModel) {
            with(binding) {
                contactFullname.text = commonModel.fullname
                contactStatus.text = commonModel.state
                contactPhoto.downloadAndSetImage(commonModel.photoUrl)
            }
        }
    }

    fun stopListener(){
        mapListeners.forEach{
            it.key.removeEventListener(it.value)
        }
    }
}


