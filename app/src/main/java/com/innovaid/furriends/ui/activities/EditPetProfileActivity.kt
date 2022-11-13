package com.innovaid.furriends.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_pet_profile.*
import kotlinx.android.synthetic.main.activity_edit_pet_profile.*


class EditPetProfileActivity : BaseActivity() {

    private lateinit var petDetails: Pet
    private var selectedImageFileUri: Uri? = null
    private var petProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet_profile)

//        if(intent.hasExtra(Constants.OTHER_PET_DETAILS)) {
//
//            petDetails = intent.getParcelableExtra(Constants.OTHER_PET_DETAILS)!!
//        }
//
//        etPetName.setText(petDetails.petName)
//        etPetBreed.setText(petDetails.petBreed)
//        tvBirthdayValue.setText(petDetails.petBirthDate)
//        etPetDescription.setText(petDetails.description)
//        GlideLoader(this).loadUserPicture(petDetails.image!!, ivPetImage)
//
//
//        ivUpdatePetImage.setOnClickListener(this)
//        btnUpdatePetProfile.setOnClickListener(this)
    }


//    override fun onClick(p0: View?) {
//        if (p0 != null) {
//            when (p0.id) {
//                R.id.ivPetImage -> {
//                    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//                        Constants.imageSelector(this)
//                    } else {
//                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//                            Constants.READ_STORAGE_PERMISSION_CODE)
//                    }
//                }
//                R.id.btnUpdatePetProfile -> {
//
//                    if (validatePetDetails()) {
//
//
//
//                        if(petProfileImageURL != null) {
//                            FirestoreClass().uploadImageToCloudStorage(this, selectedImageFileUri, Constants.PET_IMAGE)
//                        } else {
//                            updatePetProfileDetails()
//                        }
//                    }
//                }
//            }
//        }

    }
//    private fun validatePetDetails(): Boolean {
//        return when {
//
//            petProfileImageURL == null -> {
//                Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
//                false
//            }
//            etEditPetName.text.toString().trim { it <= ' ' }.isEmpty() -> {
//                Toast.makeText(this, "Please Enter your Pet's Name", Toast.LENGTH_SHORT).show()
//                false
//            }
//            etEditPetBreed.text.toString().trim { it <= ' ' }.isEmpty() -> {
//                Toast.makeText(this, "Please Enter your Pet's Breed", Toast.LENGTH_SHORT).show()
//                false
//            }
//            tvEditBirthdayValue.text.toString().trim { it <= ' ' }.isEmpty() -> {
//                Toast.makeText(this, "Please Enter your Pet's Birthdate", Toast.LENGTH_SHORT).show()
//                false
//            }
//            etEditPetColor.text.toString().trim { it <= ' ' }.isEmpty() -> {
//                Toast.makeText(this, "Please Enter your Pet's Color", Toast.LENGTH_SHORT).show()
//                false
//            }
//            spEditPetType.selectedItem.toString() == "Please Select" -> {
//                Toast.makeText(this, "Please specify your Pet's type", Toast.LENGTH_SHORT).show()
//                false
//            }
//            rgEditPetGender.checkedRadioButtonId == -1 -> {
//                Toast.makeText(this, "Please specify your Pet's type", Toast.LENGTH_SHORT).show()
//                false
//            }
//            rgEditSpayedNeutered.checkedRadioButtonId == -1 -> {
//                Toast.makeText(this, "Please specify if your Pet is spayed and neutered or not", Toast.LENGTH_SHORT).show()
//                false
//            }
//            etEditPetDescription.text.toString().trim { it <= ' ' }.isEmpty()-> {
//                Toast.makeText(this, "Please Enter your pet's description", Toast.LENGTH_SHORT).show()
//                false
//            }
//            else -> {
//                true
//            }
//        }
//
//    }
//    private fun updatePetProfileDetails() {
//        val userHashMap = HashMap<String, Any>()
//
//        val petName = etEditPetName.text.toString().trim { it <= ' '}
//        if(petName != petDetails.petName) {
//            userHashMap[Constants.PET_NAME] = petName
//        }
//
//        val petBreed =  etEditPetBreed.text.toString().trim { it <= ' '}
//        if(petBreed != petDetails.petBreed) {
//            userHashMap[Constants.PET_BREED] = petBreed
//        }
//
//        val petBirthDate =  tvEditBirthdayValue.text.toString().trim { it <= ' '}
//        if(petBirthDate != petDetails.petBirthDate) {
//            userHashMap[Constants.PET_BIRTHDATE] = petBirthDate
//        }
//        val petColor =  etEditPetColor.text.toString().trim { it <= ' '}
//        if(petColor != petDetails.petColor) {
//            userHashMap[Constants.PET_COLOR] = petColor
//        }
//        val petType =  spPetType.selectedItem.toString()
//        if(petType != petDetails.petType) {
//            userHashMap[Constants.PET_BREED] = petType
//        }
//        val selectedPetGender = findViewById<RadioButton>(rgEditPetGender.checkedRadioButtonId)
//        val petGender = selectedPetGender.text.toString().trim { it <= ' ' }
//        if(petGender != petDetails.petGender) {
//            userHashMap[Constants.PET_GENDER] = petGender
//        }
//        val selectedSpayedNeutered = findViewById<RadioButton>(rgEditSpayedNeutered.checkedRadioButtonId)
//        val spayedOrNeutered = selectedSpayedNeutered.text.toString().trim { it <= ' ' }
//        if(spayedOrNeutered != petDetails.spayedOrNeutered) {
//            userHashMap[Constants.PET_SPAYED_OR_NEUTERED] = spayedOrNeutered
//        }
//        val description =  etEditPetDescription.text.toString().trim { it <= ' '}
//        if(description != petDetails.description) {
//            userHashMap[Constants.PET_DESCRIPTION] = description
//        }
//
//        if(petProfileImageURL.isNotEmpty()) {
//            userHashMap[Constants.PET_IMAGE] = petProfileImageURL
//        }
//
//
//        FirestoreClass().updatePetProfile(this, userHashMap)
//        Toast.makeText(this, "Your details are saved", Toast.LENGTH_SHORT).show()
//    }
//    fun updatePetProfileSuccess() {
//        hideProgressDialog()
//        Toast.makeText(this, "Your profile is updated successfully" , Toast.LENGTH_SHORT)
//
//        startActivity(Intent(this, DashboardActivity::class.java))
//        finish()
//    }
