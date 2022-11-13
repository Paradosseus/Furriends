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

    //Pet

    const val PET_IMAGE: String ="petImage"
    const val USER_ID: String = "userId"

    fun imageSelector(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}