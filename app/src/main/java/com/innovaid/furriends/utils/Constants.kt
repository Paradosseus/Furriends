package com.innovaid.furriends.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.innovaid.furriends.ui.activities.BaseActivity

object Constants {

    //Collections
    const val USERS: String = "users"
    const val PETS: String = "pets"
    const val STRAY_ANIMALS: String = "stray animals"
    const val STRAY_ANIMAL_ADOPTION_FORMS: String = "applicants"

    const val FURRIENDS_PREFERENCES: String = "FurriendsPref"
    const val LOGGED_IN_USERNAME: String = "LoggedInUserName"
    const val OTHER_USER_DETAILS: String = "OtherUserDetails"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    //User
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val MOBILE: String = "phoneNumber"
    const val ADDRESS: String = "address"
    const val USER_PROFILE_IMAGE: String = "UserProfileImage"
    const val IMAGE: String = "image"
    const val BIO: String = "bio"
    const val COMPLETE_PROFILE: String = "profileCompleted"


    const val PET_IMAGE: String ="petImage"
    const val USER_ID: String = "userId"
    const val EXTRA_PET_ID = "extraPetId"
    const val EXTRA_PET_OWNER_ID = "extraOwnerId"

    const val STRAY_ANIMAL_IMAGE: String = "strayAnimalImage"
    const val EXTRA_STRAY_ANIMAL_ID = "extraPetId"
    const val EXTRA_STRAY_OWNER_ID = "userId"

    //Applicant
    const val APPLICANT_NAME: String = "applicantName"
    const val APPLICANT_ADDRESS = "applicantAddress"
    const val APPLICANT_USER_ID = "userId"
    const val EXTRA_APPLICANT_USER_ID = "extraApplicantUserId"
    const val DATE_ISSUED: String = "dateIssued"
    const val TIME_ISSUED: String = "timeIssued"
    const val REVIEW_STATUS = "reviewStatus"
    const val ANIMAL_ADOPTION_FORM: String = "strayAnimalAdoptionForm"
    const val PET_ID = "petId"
    const val EXTRA_STRAY_ID = "extraPetId"
    const val APPOINTMENT_DATE: String = "appointmentDate"
    const val APPLICATION_ID = "applicationId"
    const val EXTRA_APPLICATION_ID = "extraApplicationId"






//    const val OTHER_PET_DETAILS: String = "OtherPetDetails"
//    const val PET_NAME: String ="petName"
//    const val PET_BREED: String ="petBreed"
//    const val PET_BIRTHDATE: String ="petBirthDate"
//    const val PET_COLOR: String ="petColor"
//    const val PET_TYPE: String ="petType"
//    const val PET_GENDER: String ="petGender"
//    const val PET_SPAYED_OR_NEUTERED: String ="spayedOrNeutered"
//    const val PET_DESCRIPTION: String ="description"
//    const val PET_STATUS: String ="status"



    fun imageSelector(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}