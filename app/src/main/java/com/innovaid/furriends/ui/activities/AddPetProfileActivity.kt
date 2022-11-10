package com.innovaid.furriends.ui.activities


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.innovaid.furriends.R
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_pet_profile.*
import kotlinx.android.synthetic.main.activity_set_up_user_profile.*
import java.io.IOException

class AddPetProfileActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet_profile)
        ivAddPetImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.ivAddPetImage -> {
                    if(ContextCompat.checkSelfPermission(
                            this,android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.imageSelector(this)
                    } else {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE)
                    }

                }
            }
        }
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
                    mSelectedImageFileUri = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, ivPetImage)
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

    private fun validatePetDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
                false
            }
            etPetName.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your Pet's Name", Toast.LENGTH_SHORT).show()
                false
            }
            tvBirthdayValue.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your Pet's Birthdate", Toast.LENGTH_SHORT).show()
                false
            }
            etPetColor.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your Pet's Color", Toast.LENGTH_SHORT).show()
                false
            }
            spPetType.selectedItem.toString().trim { it <= ' ' } == "" -> {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
                false
            }
            etAddress.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
                false
            }
            etAddress.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show()
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
}