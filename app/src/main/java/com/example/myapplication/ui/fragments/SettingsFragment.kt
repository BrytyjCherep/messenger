package com.example.myapplication.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.activities.RegisterActivity
import com.example.myapplication.databinding.FragmentSettingsBinding
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.utilits.AUTH
import com.example.myapplication.utilits.AppStates
import com.example.myapplication.utilits.CHILD_PHOTO_URL
import com.example.myapplication.utilits.FOLDER_PROFILE_IMAGE
import com.example.myapplication.utilits.REF_STORAGE_ROOT
import com.example.myapplication.utilits.CURRENT_UID
import com.example.myapplication.utilits.NODE_USERS
import com.example.myapplication.utilits.REF_DATABASE_ROOT
import com.example.myapplication.utilits.USER
import com.example.myapplication.utilits.downloadAndSetImage
import com.example.myapplication.utilits.getUrlFromStorage
import com.example.myapplication.utilits.putImageToStorage
import com.example.myapplication.utilits.putUrlToDatabase
import com.example.myapplication.utilits.replaceActivity
import com.example.myapplication.utilits.replaceFragment
import com.example.myapplication.utilits.showToast
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        binding.settingsBio.text = USER.bio
        binding.settingsFullName.text = USER.fullname
        binding.settingsPhoneNumber.text = USER.phone
        binding.settingsStatus.text = USER.phone


        binding.settingsUsername.text = USER.username
        binding.settingsBtnChangeUsername.setOnClickListener{
            replaceFragment(ChangeUsernameFragment())
        }
        binding.settingsBtnChangeBio.setOnClickListener{
            replaceFragment(ChangeBioFragment())
        }
        binding.settingsChangePhoto.setOnClickListener{
            changePhotoUser()
        }
        binding.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(600,600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.settings_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                (APP_ACTIVITY).replaceActivity(RegisterActivity())
            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment())

        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)
            
            putImageToStorage(uri, path){
                getUrlFromStorage(path){
                    putUrlToDatabase(it){
                        binding.settingsUserPhoto.downloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl = it
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }
        }
    }


}