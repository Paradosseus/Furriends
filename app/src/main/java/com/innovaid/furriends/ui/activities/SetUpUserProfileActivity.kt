package com.innovaid.furriends.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.innovaid.furriends.MainActivity
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.User
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_set_up_user_profile.*
import java.io.IOException
import java.util.jar.Manifest

class SetUpUserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var userDetails: User
    private var selectedImageFileUri: Uri? = null
    private var userProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up_user_profile)


        if(intent.hasExtra(Constants.OTHER_USER_DETAILS)) {

            userDetails = intent.getParcelableExtra(Constants.OTHER_USER_DETAILS)!!
        }

        etFirstName.isEnabled = false
        etFirstName.setText(userDetails.firstName)

        etLastName.isEnabled = false
        etLastName.setText(userDetails.lastName)

        etEmail.isEnabled = false
        etEmail.setText(userDetails.email)

        ivUserPhoto.setOnClickListener(this)
        btnSaveProfile.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.ivUserPhoto -> {
                    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Constants.imageSelector(this)
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btnSaveProfile -> {

                    if (validateUserProfileDetails()) {

                        showProgressDialog(resources.getString(R.string.please_wait))

                        if(selectedImageFileUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(this, selectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }
    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val phoneNumber = etPhoneNumber.text.toString().trim{ it <= ' '}
        val address = etAddress.text.toString().trim{ it <= ' '}

        if (phoneNumber.isNotEmpty()) {
            userHashMap[Constants.MOBILE] = phoneNumber.toLong()
        }
        if (address.isNotEmpty()) {
            userHashMap[Constants.ADDRESS] = address
        }
        if(userProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = userProfileImageURL
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1
        //showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfile(this, userHashMap)
        Toast.makeText(this, "Your details are saved", Toast.LENGTH_SHORT).show()
    }
    fun updateUserProfileSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Your profile has been updated successfully" , Toast.LENGTH_SHORT)

        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission is granted", Toast.LENGTH_SHORT).show()
                Constants.imageSelector(this)
            } else {
                Toast.makeText(this, "You denied the storage permission. Go to settings to allow it.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        selectedImageFileUri = data.data!!
//                        ivUserPhoto.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(selectedImageFileUri!!, ivUserPhoto)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Image Selection Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    private fun validateUserProfileDetails(): Boolean {
        return when {

            etPhoneNumber.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show()
                false
            }
            etAddress.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL: String) {
        //hideProgressDialog()
        userProfileImageURL = imageURL
        updateUserProfileDetails()
    }

}