package com.example.myapplication.utilits

import android.net.Uri
import android.provider.ContactsContract
import androidx.core.database.getStringOrNull
import com.example.myapplication.models.CommonModel
import com.example.myapplication.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var AUTH:FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER:UserModel
lateinit var CURRENT_UID:String

const val NODE_USERS="users"
const val  NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val FOLDER_PROFILE_IMAGE = "profile_image"
const val NODE_PHONES_CONTACTS = "phones_contacts"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"

fun initFirebase(){
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener{showToast(it.message.toString())}
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url:String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener{showToast(it.message.toString())}
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener{showToast(it.message.toString())}
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener{
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()){
                USER.username = CURRENT_UID
            }
            function()
        })
}

fun initContacts() {
    if (checkPermissions(READ_CONTACTS)){
        var arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()){
                val fullName = it.getStringOrNull(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = it.getStringOrNull(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName.toString()
                newModel.phone = phone.toString().replace(Regex("[\\s,-]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhonesToDatabase(arrayContacts)
    }
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    REF_DATABASE_ROOT.child(NODE_PHONES)
        .addListenerForSingleValueEvent(AppValueEventListener{
            it.children.forEach {snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone){
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
                }
            }
        })
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java)?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java)?: UserModel()
