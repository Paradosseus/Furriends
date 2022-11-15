package com.innovaid.furriends.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.*
import com.innovaid.furriends.ui.fragments.HomeFragment
import com.innovaid.furriends.ui.fragments.InboxFragment
import com.innovaid.furriends.ui.fragments.ListingsFragment
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
                    is RegisterActivity -> {
                        activity.userRegisteredSuccessful(user)
                    }
                    is ProfileActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                    is DashboardActivity -> {
                        activity.userDetailsSuccess(user)
                    }

                }

            }
            .addOnFailureListener { e ->
                when(activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is RegisterActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is DashboardActivity -> {
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
                    is EditProfileActivity -> {
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
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child( imageType + "." + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageFileURI)
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
                        is EditProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddPetProfileActivity -> {
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
                    is EditProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddPetProfileActivity -> {
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

    fun uploadPetDetails(activity: AddPetProfileActivity, petInfo: Pet) {
        fireStore.collection(Constants.PETS)
            .document()
            .set(petInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.petUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the pet details",
                    e
                )
            }
    }

    fun getPetsList(fragment: Fragment) {
        fireStore.collection(Constants.PETS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e("Pets List", document.documents.toString())

                val petsList: ArrayList<Pet> = ArrayList()
                for(i in document.documents) {

                    val pet = i.toObject(Pet::class.java)
                    pet!!.petId = i.id

                    petsList.add(pet)
                }

                when(fragment) {
                    is ListingsFragment -> {
                        fragment.petListLoadedSuccessfullyFromFireStore(petsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is ListingsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting product list.", e)
            }

    }
    fun getPetDetails(activity: PetDetailsActivity, petId: String) {
        fireStore.collection(Constants.PETS)
            .document(petId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val pet = document.toObject(Pet::class.java)
                if (pet != null) {
                    activity.petDetailsSuccess(pet)
                }

            }
            .addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the pet details", e)

            }
    }
    fun deletePet(fragment: ListingsFragment, petId: String) {
        fireStore.collection(Constants.PETS)
            .document(petId)
            .delete()
            .addOnSuccessListener {
                fragment.deletePetSuccess()
            }

            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the profile", e
                )
            }
    }

    fun getPetsListToHome(fragment: HomeFragment) {
        fireStore.collection(Constants.PETS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val petsList: ArrayList<Pet> = ArrayList()

                for(i in document.documents) {

                    val pet = i.toObject(Pet::class.java)!!
                    pet.petId = i.id
                    petsList.add(pet)
                }
                fragment.petListSuccessfullyLoadedToHome(petsList)
            }
            .addOnFailureListener {
                e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting pets list", e)
            }

    }

    fun getPetDetailsForEdit(activity: EditPetProfileActivity, petId: String) {
        fireStore.collection(Constants.PETS)
            .document(petId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val pet = document.toObject(Pet::class.java)
                if (pet != null) {
                    activity.petDetailsSuccessForEdit(pet)
                }

            }
            .addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the pet details", e)

            }
    }

}
