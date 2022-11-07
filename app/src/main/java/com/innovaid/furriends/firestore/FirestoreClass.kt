package com.innovaid.furriends.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.LoginActivity
import com.innovaid.furriends.ui.activities.RegisterActivity
import com.innovaid.furriends.ui.activities.SetUpUserProfileActivity
import com.innovaid.furriends.utils.Constants

class FirestoreClass {
    private val fireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        fireStore.collection(Constants.USERS)
            .document(userInfo.id)

            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName, "Error while registering the user.", e
                )
            }
    }
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserInfo(activity: Activity) {
        fireStore.collection(Constants.USERS)

            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreference =
                    activity.getSharedPreferences(
                        Constants.FURRIENDS_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreference.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when(activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccessful(user)
                    }
                }

            }
            .addOnFailureListener { e ->
                when(activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName, "Error while getting user details", e
                )
            }
    }
    fun updateUserProfile(activity: Activity, userHashMap: HashMap<String, Any>) {
        fireStore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity) {
                    is SetUpUserProfileActivity -> {

                        activity.updateUserProfileSuccess()
                    }
                }
            }
            .addOnFailureListener{ e->
                when(activity) {
                    is SetUpUserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName, "Error while updating the user details", e
                )
            }

    }
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child( Constants.USER_PROFILE_IMAGE + "." + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageFileURI)
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener { takeSnapshot ->
            Log.e(
                "Firebase Image URL",
                takeSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            takeSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when (activity) {
                        is SetUpUserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }

                }
        }
            .addOnFailureListener() { exception ->
                when(activity) {
                    is SetUpUserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }
}