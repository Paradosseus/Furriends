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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.innovaid.furriends.ReviewApplicationActivity
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.*
import com.innovaid.furriends.ui.activities.admin.*
import com.innovaid.furriends.ui.activities.user.*
import com.innovaid.furriends.ui.fragments.admin.AdminHomeFragment
import com.innovaid.furriends.ui.fragments.admin.AdminStrayListingsFragment
import com.innovaid.furriends.ui.fragments.user.UserHomeFragment
import com.innovaid.furriends.ui.fragments.user.UserListingsFragment
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
                    is UserProfileActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                    is UserDashboardActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                    is AdminDashboardActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                    is StrayAdoptionActivity -> {
                        activity.uploadStrayApplicationFormDetails(user)
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
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is UserDashboardActivity -> {
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
                    is EditUserProfileActivity -> {
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
    fun uploadStrayAnimalAdoptionToStorage(activity: Activity, pdfUri: Uri) {
        val sRef = FirebaseStorage.getInstance().reference.child("${System.currentTimeMillis()}.pdf")
        val uploadTask = sRef.putFile(pdfUri)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            sRef.downloadUrl
        }.addOnCompleteListener { task ->
            when(activity) {
                is StrayAdoptionActivity -> {
                    val downloadUrl = task.result
                    activity.uploadPdfSuccess(downloadUrl.toString())
                }
            }

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
                        is EditUserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddUserPetProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddStrayAnimalProfileActivity -> {
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
                    is EditUserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddUserPetProfileActivity -> {
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
    fun uploadStrayAdoptionFormDetails(activity: StrayAdoptionActivity, adoptionFormInfo: StrayAdoptionForm) {
        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .document()
            .set(adoptionFormInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.uploadStrayApplicationFormDetailsSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading your form",
                    e
                )
            }

    }
    fun uploadPetDetails(activity: AddUserPetProfileActivity, petInfo: Pet) {
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
    fun uploadStrayAnimalDetails(activity: AddStrayAnimalProfileActivity, strayAnimalInfo: StrayAnimal) {
        fireStore.collection(Constants.STRAY_ANIMALS)
            .document()
            .set(strayAnimalInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.strayAnimalUploadSuccess()
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

    fun getApplicationsList(activity: Activity) {
        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .whereEqualTo(Constants.REVIEW_STATUS, "being reviewed")
            .get()
            .addOnSuccessListener {  document ->

                Log.e("Applicants List", document.documents.toString())

                val applicantsList: ArrayList<StrayAdoptionForm> = ArrayList()
                for (i in document.documents) {

                    val applicant = i.toObject(StrayAdoptionForm::class.java)
                    applicant!!.applicantUserId = i.id

                    applicantsList.add(applicant)
                }
                when(activity) {
                    is ReviewApplicationActivity -> {
                        activity.applicantsListLoadedSuccessfullyFromFirestore(applicantsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is ReviewApplicationActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Application List", "Error while getting applicant list.", e)

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
                    is UserListingsFragment -> {
                        fragment.petListLoadedSuccessfullyFromFireStore(petsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is UserListingsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting product list.", e)
            }

    }
    fun getStrayAnimalList(fragment: Fragment) {
        fireStore.collection(Constants.STRAY_ANIMALS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e("Stray List List", document.documents.toString())

                val strayAnimalList: ArrayList<StrayAnimal> = ArrayList()
                for(i in document.documents) {

                    val strayAnimal = i.toObject(StrayAnimal::class.java)
                    strayAnimal!!.strayAnimalId = i.id

                    strayAnimalList.add(strayAnimal)
                }

                when(fragment) {
                    is AdminStrayListingsFragment -> {
                        fragment.strayAnimalListLoadedSuccessfullyFromFireStore(strayAnimalList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is AdminStrayListingsFragment -> {
                        fragment.hideProgressDialog()
                    }
                    is AdminHomeFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting product list.", e)
            }

    }
    fun getApplicantDetails(activity: ApplicantDetailsActivity, applicantId: String, strayId: String) {

        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS).document(applicantId).get()
            .addOnSuccessListener { document ->
                Log.e(javaClass.simpleName, document.toString())
                val applicant = document.toObject(StrayAdoptionForm::class.java)
                if(applicant != null) {
                    fireStore.collection(Constants.STRAY_ANIMALS).document(strayId).get()
                        .addOnSuccessListener { document ->
                            Log.d(javaClass.simpleName, document.toString())
                            val strayAnimal = document.toObject(StrayAnimal::class.java)
                            if (strayAnimal != null) {
                                activity.applicantDetailSuccess(applicant, strayAnimal)
                            }
                        }
                }

            }




    }
    fun getPetDetails(activity: UserPetDetailsActivity, petId: String) {
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
    fun getStrayAnimalDetails(activity: StrayAnimalDetailsActivity, strayAnimalId: String) {
        fireStore.collection(Constants.STRAY_ANIMALS)
            .document(strayAnimalId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val strayAnimal = document.toObject(StrayAnimal::class.java)
                if (strayAnimal != null) {
                    activity.strayAnimalSuccess(strayAnimal)
                }
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the stray animal details", e)
            }
    }
    fun deletePet(fragment: UserListingsFragment, petId: String) {
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
    fun deleteStrayAnimal(fragment: AdminStrayListingsFragment, strayAnimalId: String) {
        fireStore.collection(Constants.STRAY_ANIMALS)
            .document(strayAnimalId)
            .delete()
            .addOnSuccessListener {
                fragment.deleteStrayAnimalSuccess()
            }

            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the profile", e
                )
            }
    }

    fun getPetsListToHome(fragment: UserHomeFragment) {
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
    fun getStrayAnimalsListToHome(fragment: AdminHomeFragment) {
        fireStore.collection(Constants.STRAY_ANIMALS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val strayAnimalsList: ArrayList<StrayAnimal> = ArrayList()

                for(i in document.documents) {

                    val strayAnimal = i.toObject(StrayAnimal::class.java)!!
                    strayAnimal.strayAnimalId = i.id
                    strayAnimalsList.add(strayAnimal)
                }
                fragment.strayAnimalListSuccessfullyLoadedToHome(strayAnimalsList)
            }
            .addOnFailureListener {
                    e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting pets list", e)
            }

    }

    fun getPetDetailsForEdit(activity: EditUserPetProfileActivity, petId: String) {
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
