package com.innovaid.furriends.ui.activities.user


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_user_pet_profile.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddUserPetProfileActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri: Uri? = null
    private var mPetImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_pet_profile)


        ivAddPetImage.setOnClickListener(this)
        btnSetupPetProfile.setOnClickListener(this)
        ivCalendar.setOnClickListener(this)
        ibAPDBackButton.setOnClickListener(this)
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
                R.id.btnSetupPetProfile -> {
                    if (validatePetDetails()) {

                        uploadPetImage()
                    }
                }
                R.id.ivCalendar -> {
                    setDate()
                }
                R.id.ibAPDBackButton -> {
                    onBackPressed()
                }

            }
        }
    }
    private fun setDate() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateTable(myCalendar)
        }
        DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateTable(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        tvBirthdayValue.setText(sdf.format(myCalendar.time))
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
            etPetBreed.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your Pet's Breed", Toast.LENGTH_SHORT).show()
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
            spPetType.selectedItem.toString() == "Please Select" -> {
                Toast.makeText(this, "Please specify your Pet's type", Toast.LENGTH_SHORT).show()
                false
            }
            rgPetGender.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please specify your Pet's type", Toast.LENGTH_SHORT).show()
                false
            }
            rgSpayedNeutered.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please specify if your Pet is spayed and neutered or not", Toast.LENGTH_SHORT).show()
                false
            }
            etPetDescription.text.toString().trim { it <= ' ' }.isEmpty()-> {
                Toast.makeText(this, "Please Enter your pet's description", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }

    }
    private fun uploadPetDetails() {

        val username = this.getSharedPreferences(Constants.FURRIENDS_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!
        val petGender = findViewById<RadioButton>(rgPetGender.checkedRadioButtonId)
        val petSpayedOrNeutered = findViewById<RadioButton>(rgSpayedNeutered.checkedRadioButtonId)


        val pet = Pet(
            FirestoreClass().getCurrentUserID(),
            username,
            etPetName.text.toString().trim { it <= ' ' },
            etPetBreed.text.toString().trim { it <= ' ' },
            tvBirthdayValue.text.toString().trim { it <= ' ' },
            etPetColor.text.toString().trim { it <= ' ' },
            spPetType.selectedItem.toString(),
            petGender.text.toString().trim { it <= ' ' },
            petSpayedOrNeutered.text.toString().trim { it <= ' ' },
            etPetDescription.text.toString().trim { it <= ' '},
            mPetImageURL,
            "Pending",
            "Listed",
            "userPet"
        )

        FirestoreClass().uploadPetDetails(this@AddUserPetProfileActivity, pet)
    }

    private fun uploadPetImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(
            this@AddUserPetProfileActivity,
            mSelectedImageFileUri,
            Constants.PET_IMAGE
        )
    }

    fun imageUploadSuccess(imageURL: String) {

        mPetImageURL = imageURL

        uploadPetDetails()
    }
    fun petUploadSuccess() {

        hideProgressDialog()

        Toast.makeText(this, "Your pet is uploaded successfully", Toast.LENGTH_SHORT).show()

        finish()
    }
}