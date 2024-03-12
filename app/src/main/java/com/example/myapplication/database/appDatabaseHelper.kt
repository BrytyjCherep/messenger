package com.example.myapplication.database

import android.net.Uri
import com.example.myapplication.R
import com.example.myapplication.models.CommonModel
import com.example.myapplication.models.UserModel
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.AppValueEventListener
import com.example.myapplication.utilits.TYPE_MESSAGE_IMAGE
import com.example.myapplication.utilits.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel
lateinit var CURRENT_UID: String

const val TYPE_TEXT = "text"

const val NODE_USERS = "users"
const val NODE_MESSAGES = "messages"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_MESSAGE_IMAGE = "message_image"
const val NODE_PHONES_CONTACTS = "phones_contacts"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"

const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"
const val CHILD_FILE_URL = "fileUrl"

fun initFirebase() {
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
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}


fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES)
            .addListenerForSingleValueEvent(AppValueEventListener {
                it.children.forEach { snapshot ->
                    arrayContacts.forEach { contact ->
                        if (snapshot.key == contact.phone) {
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                                .child(snapshot.value.toString()).child(CHILD_ID)
                                .setValue(snapshot.value.toString())
                                .addOnFailureListener { showToast(it.message.toString()) }

                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                                .child(snapshot.value.toString()).child(CHILD_FULLNAME)
                                .setValue(contact.fullname)
                                .addOnFailureListener { showToast(it.message.toString()) }
                        }
                    }
                }
            })
    }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun sendMessage(message: String, receivingUserId: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun updateCurrentUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
        .setValue(newUserName)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                deleteOldUsername(newUserName)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

private fun deleteOldUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {

            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUserName

        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(newBio)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setNameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            USER.fullname = fullname
            APP_ACTIVITY.mAppDrawer.updateHeader()
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun sendMessageAsImage(receivingUserId: String, imageUrl: String, messageKey: String) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = TYPE_MESSAGE_IMAGE
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = imageUrl

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getMessageKey(id: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    .child(id).push().key.toString()

fun uploadFileToStorage(uri: Uri, messageKey: String) {
    showToast("Record ok")
}
