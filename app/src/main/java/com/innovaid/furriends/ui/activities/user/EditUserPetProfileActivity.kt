package com.innovaid.furriends.ui.activities.user

import android.app.Activity
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
import kotlinx.android.synthetic.main.activity_edit_user_pet_profile.*
import java.io.IOException


class EditUserPetProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var petDetails: Pet
    private var mPetId: String = ""
    private var mSelectedImageFileUri: Uri? = null
    private var mPetImageURL: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_pet_profile)

        if (intent.hasExtra(Constants.EXTRA_PET_ID)) {
            mPetId = intent.getStringExtra(Constants.EXTRA_PET_ID)!!
            Log.i("Pet id", mPetId)
        }
        getPetDetailsForEdit()

        ivEditUpdatePetImage.setOnClickListener(this)
        btnUpdatePetProfile.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.ivEditUpdatePetImage -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        Constants.imageSelector(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btnUpdatePetProfile -> {
                    if (validatePetDetails()) {
                        uploadPetImage()

                    }
                }
            }
        }
    }

    fun getPetDetailsForEdit() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPetDetailsForEdit(this, mPetId)
    }

    fun petDetailsSuccessForEdit(pet: Pet) {
        hideProgressDialog()
        GlideLoader(this@EditUserPetProfileActivity).loadPetPicture(pet.image!!, ivEditPetImage)
        etEditPetName.setText(pet.petName)
        etEditPetBreed.setText(pet.petBreed)
        tvEditBirthdayValue.setText(pet.petBirthDate)
        etEditPetColor.setText(pet.petColor)
        //Add Location
        etEditPetDescription.setText(pet.description)
    }

    private fun validatePetDetails(): Boolean {
        return when {

            etEditPetName.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your Pet's Name", Toast.LENGTH_SHORT).show()
                false
            }
            etEditPetBreed.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your Pet's Breed", Toast.LENGTH_SHORT).show()
                false
            }
            tvEditBirthdayValue.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your Pet's Birthdate", Toast.LENGTH_SHORT).show()
                false
            }
            etEditPetColor.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your Pet's Color", Toast.LENGTH_SHORT).show()
                false
            }
            spEditPetType.selectedItem.toString() == "Please Select" -> {
                Toast.makeText(this, "Please specify your Pet's type", Toast.LENGTH_SHORT).show()
                false
            }
            rgEditPetGender.checkedRadioButtonId == -1 -> {
                Toast.makeText(this, "Please specify your Pet's type", Toast.LENGTH_SHORT).show()
                false
            }
            rgEditSpayedNeutered.checkedRadioButtonId == -1 -> {
                Toast.makeText(
                    this,
                    "Please specify if your Pet is spayed and neutered or not",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            etEditPetDescription.text.toString().trim { it <= ' ' }.isEmpty() -> {
                Toast.makeText(this, "Please Enter your pet's description", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            else -> {
                true
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

    private fun uploadPetImage() {

        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null) {
            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.PET_IMAGE)
        } else {
            uploadPetDetailsFromEdit()
        }


    }

    fun imageUploadSuccess(imageURL: String) {

        mPetImageURL = imageURL

        uploadPetDetailsFromEdit()
    }

    private fun uploadPetDetailsFromEdit() {

        val petHashMap = HashMap<String, Any>()

        val petName = etEditPetName.text.toString().trim { it <= ' ' }
        if (petName != petDetails.petName) {
            petHashMap[Constants.PET_NAME] = petName
        }

        val petBreed = etEditPetBreed.text.toString().trim { it <= ' ' }
        if (petBreed != petDetails.petBreed) {
            petHashMap[Constants.PET_BREED] = petBreed
        }

        val petBirthdate = tvEditBirthdayValue.text.toString().trim { it <= ' ' }
        if (petBirthdate != petBirthdate) {
            petHashMap[Constants.PET_BIRTHDATE] = petBirthdate
        }

        val petType = spEditPetType.selectedItem.toString()
        if (petType != petDetails.petType) {
            petHashMap[Constants.PET_TYPE] = petType
        }
        val petGender = findViewById<RadioButton>(rgEditPetGender.checkedRadioButtonId)
        if (petGender.text.toString().trim { it <= ' ' } != petDetails.petGender) {
            petHashMap[Constants.PET_GENDER] = petGender
        }
        val spayedOrNeutered = findViewById<RadioButton>(rgEditSpayedNeutered.checkedRadioButtonId)
        if (spayedOrNeutered.text.toString().trim { it <= ' ' } != petDetails.spayedOrNeutered) {
            petHashMap[Constants.PET_SPAYED_OR_NEUTERED] = spayedOrNeutered
        }
        val description = etEditPetDescription.text.toString().trim { it <= ' ' }
        if (description != petDetails.description) {
            petHashMap[Constants.PET_DESCRIPTION] = description
        }
        if (mPetImageURL.isNotEmpty()) {
            petHashMap[Constants.PET_IMAGE] = mPetImageURL
        }

        FirestoreClass().updatePetProfile(this, petHashMap, mPetId)
    }

    fun updatePetProfileSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Your pet's profile has been updated successfully", Toast.LENGTH_SHORT)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

