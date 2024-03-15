package com.example.myapplication.ui.screens

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSettingsBinding
import com.example.myapplication.utilits.APP_ACTIVITY
import com.example.myapplication.database.AUTH
import com.example.myapplication.utilits.AppStates
import com.example.myapplication.database.CURRENT_UID
import com.example.myapplication.database.FOLDER_PROFILE_IMAGE
import com.example.myapplication.database.REF_STORAGE_ROOT
import com.example.myapplication.database.USER
import com.example.myapplication.utilits.downloadAndSetImage
import com.example.myapplication.database.getUrlFromStorage
import com.example.myapplication.database.putFileToStorage
import com.example.myapplication.database.putUrlToDatabase
import com.example.myapplication.utilits.replaceFragment
import com.example.myapplication.utilits.restartActivity
import com.example.myapplication.utilits.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)

            putFileToStorage(uri, path){
                getUrlFromStorage(path){
                    putUrlToDatabase(it){
                        binding.settingsUserPhoto.downloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl = it
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }
        } else {
            showToast("Файл не выбран")
        }
    }

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
        /*//создание файла с названием sdfg в папке Downloads
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "sdfg"
        )
        file.createNewFile()*/
    }

    private fun changePhotoUser() {
        //attachFile() //вызов функции добавления файла
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(250,250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
        /*pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))*/
    }

    //функция открытия окна для добавления файла
    private fun attachFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, 301)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.settings_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
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
            
            putFileToStorage(uri, path){
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

    /*//добавление файла
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data!=null){
            when(requestCode){
                301 -> {
                    val uri = data?.data //ссылка на файл
                    showToast(uri.toString())
                }
            }
        }
    }*/


}