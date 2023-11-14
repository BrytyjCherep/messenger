package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ContactItemBinding
import com.example.myapplication.databinding.FragmentContactsBinding
import com.example.myapplication.models.CommonModel
import com.example.myapplication.ui.fragments.single_chat.SingleChatFragment
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.utilits.CURRENT_UID
import com.example.myapplication.utilits.NODE_PHONES_CONTACTS
import com.example.myapplication.utilits.NODE_USERS
import com.example.myapplication.utilits.REF_DATABASE_ROOT
import com.example.myapplication.utilits.downloadAndSetImage
import com.example.myapplication.utilits.getCommonModel
import com.example.myapplication.utilits.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var b: ContactItemBinding

    private lateinit var mRecyclerView: RecyclerView
    //private lateinit var mAdapter: CustomAdapter
    private lateinit var mRefContacts: DatabaseReference

    //From
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel,ContactsHolder>
    private lateinit var mRefUsers:DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    //Down this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ContactItemBinding.inflate(layoutInflater)
        val view =b.root
    }
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

        //From
        mAdapter = object :FirebaseRecyclerAdapter<CommonModel,ContactsHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item,parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

                mRefUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()
                    if(contact.fullname.isEmpty()){
                        holder.name.text = model.fullname
                    } else{
                        holder.name.text = contact.fullname
                    }
                    holder.status.text = contact.state
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener { replaceFragment(SingleChatFragment(model)) }
                }

                mRefUsers.addValueEventListener(mRefUsersListener)
                mapListeners[mRefUsers] = mRefUsersListener


            }

        }
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter.startListening()

        //down this


        /*mAdapter = CustomAdapter(options)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter.startListening()*/
    }

    //From
    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name:TextView = view.findViewById(R.id.contact_fullname)
        val status:TextView = view.findViewById(R.id.contact_status)
        val photo:CircleImageView = view.findViewById(R.id.contact_photo)

    }
    /*class ContactsHolder(_binding: ContactItemBinding) : RecyclerView.ViewHolder(_binding.root) {
        var binding = ContactItemBinding.bind(itemView)

        fun bind(commonModel: CommonModel) {
            with(binding) {
                contactFullname.text = commonModel.fullname
                contactStatus.text = commonModel.state
                contactPhoto.downloadAndSetImage(commonModel.photoUrl)

            }
        }
    }*/
    //down this

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        //mAdapter.stopListener()

        //From
        mapListeners.forEach{
            it.key.removeEventListener(it.value)
        }
        //down this
    }
}

