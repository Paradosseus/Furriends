package com.innovaid.furriends.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.innovaid.furriends.ReviewApplicationActivity
import com.innovaid.furriends.models.*
import com.innovaid.furriends.ui.activities.*
import com.innovaid.furriends.ui.activities.admin.*
import com.innovaid.furriends.ui.activities.user.*
import com.innovaid.furriends.ui.adapters.UserAdapter
import com.innovaid.furriends.ui.fragments.SearchUserChatFragment
import com.innovaid.furriends.ui.fragments.admin.*
import com.innovaid.furriends.ui.fragments.user.*
import com.innovaid.furriends.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirestoreClass {

    private val fireStore = FirebaseFirestore.getInstance()
    var num = 10
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
    private fun generateTimestamp(): String {
        val current = Calendar.getInstance().time
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return format.format(current)
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

                when (activity) {
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
                when (activity) {
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

    fun getOtherUserDetails(activity: Activity, otherUserId: String) {
        fireStore.collection(Constants.USERS)
            .document(otherUserId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)
                if (user != null) {
                    when(activity) {
                        is MessageActivity -> {
                            activity.otherUserDetailsLoadedSuccess(user)
                        }
                    }

                }
            }
    }

    fun updateUserProfile(activity: Activity, userHashMap: HashMap<String, Any>) {
        fireStore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is SetUpUserProfileActivity -> {
                        activity.updateUserProfileSuccess()
                    }
                    is EditUserProfileActivity -> {
                        activity.updateUserProfileSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
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
        val sRef =
            FirebaseStorage.getInstance().reference.child("${System.currentTimeMillis()}.pdf")
        val uploadTask = sRef.putFile(pdfUri)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            sRef.downloadUrl
        }.addOnCompleteListener { task ->
            when (activity) {
                is StrayAdoptionActivity -> {
                    val downloadUrl = task.result
                    activity.uploadPdfSuccess(downloadUrl.toString())
                }
            }

        }

    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + "." + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileURI
            )
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
                when (activity) {
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
                fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
                    .whereEqualTo(Constants.APPLICANT_USER_ID, getCurrentUserID())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for(document in querySnapshot) {
                            Log.e(javaClass.simpleName, document.toString())
                            val strayAdoption = document.toObject(StrayAdoptionForm::class.java)
                            strayAdoption!!.applicationId = document.id
                            if (strayAdoption != null) {
                                when(activity) {
                                    is StrayAdoptionActivity -> {
                                        activity.uploadStrayApplicationFormDetailsSuccess(strayAdoption)
                                    }
                                }

                            }
                        }

                    }
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
    fun uploadPetAdoptionFormDetails(activity: UserAdoptionActivity, userAdoptionFormInfo: UserAdoptionForm) {
        fireStore.collection(Constants.USER_PET_ADOPTION_FORM)
            .document()
            .set(userAdoptionFormInfo, SetOptions.merge())
            .addOnSuccessListener {
                fireStore.collection(Constants.USER_PET_ADOPTION_FORM)
                    .whereEqualTo(Constants.APPLICANT_USER_ID, getCurrentUserID())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for(document in querySnapshot) {
                            Log.e(javaClass.simpleName, document.toString())
                            val petAdoption = document.toObject(UserAdoptionForm::class.java)
                            petAdoption.applicationId = document.id
                            if(petAdoption != null) {
                                when(activity) {
                                    is UserAdoptionActivity -> {
                                        activity.uploadPetAdoptionFormDetailsSuccess(petAdoption)
                                    }
                                }
                            }
                        }
                    }
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

    fun uploadStrayAnimalDetails(
        activity: AddStrayAnimalProfileActivity,
        strayAnimalInfo: StrayAnimal
    ) {
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



    fun getChatId(senderId: String, receiverId: String): String {
        return if (senderId < receiverId) {
            "$senderId-$receiverId"
        } else {
            "$receiverId-$senderId"
        }
    }

    fun getChatList(activity: MessageActivity, senderId: String, receiverId: String) {
        fireStore.collection(Constants.CHATS)
            .document(getChatId(senderId, receiverId))
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.e("MessageActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    val chatList = ArrayList<Chat>()
                    for (document in querySnapshot) {
                        val chat = document.toObject(Chat::class.java)
                        chat.messageId = document.id

                        chatList.add(chat)
                    }
                    when (activity) {
                        is MessageActivity -> {
                            activity.chatListLoadedSuccessfully(chatList)
                        }
                    }
                }
            }
    }
    fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        val chatRef = fireStore.collection(Constants.CHATS).document(getChatId(senderId, receiverId))
        val messagesRef = chatRef.collection("messages")

        val chat = hashMapOf(
            "sender" to senderId,
            "receiver" to receiverId,
            "message" to message,
            "isSeen" to false,
            "imageUrl" to "",
            "timestamp" to generateTimestamp()

        )
        messagesRef.add(chat)
        val receiver = chat["receiver"].toString()
        val message = chat["message"].toString()
        val timestamp = chat["timestamp"].toString()


        fireStore.collection(Constants.USERS)
            .document(receiver)
            .get()
            .addOnSuccessListener { userDocument ->
                val user = userDocument.toObject(User::class.java)
                if(user != null) {
                    val ref = fireStore.collection(Constants.RECENT_CHATS).document(getChatId(getCurrentUserID(), user.id))
                    val prop = hashMapOf(
                        "sender" to senderId,
                        "receiver" to receiverId,
                    )
                    ref.set(prop)

                    fireStore.collection(Constants.RECENT_CHATS).document(getChatId(getCurrentUserID(), user.id)).collection("messages")
                        .get()
                        .addOnSuccessListener { recentChats ->
                            if (recentChats.isEmpty) {
                                fireStore.collection(Constants.RECENT_CHATS).document(getChatId(getCurrentUserID(), user.id)).collection("messages")
                                    .add(
                                        RecentChats(
                                            user.id,
                                            getCurrentUserID(),
                                            user.firstName,
                                            user.lastName,
                                            user.image,
                                            message,
                                            timestamp,

                                        )
                                    )
                            } else {
                                recentChats.documents[0].reference.update("lastMessage", message)
                                recentChats.documents[0].reference.update("timestamp", timestamp)
                            }
                        }
                }

            }
    }
//    fun addToRecentChats(fragment: ChatsListFragment) {
//        val recentChatList: ArrayList<RecentChats> = ArrayList()
//        fireStore.collection(Constants.RECENT_CHATS)
//            .whereEqualTo("sender" , getCurrentUserID())
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                if(querySnapshot != null) {
//                    for (document in querySnapshot) {
//                        document.reference.collection("messages")
//                            .get()
//                            .addOnSuccessListener { document1 ->
//                                Log.e("User Application List", document1.documents.toString())
//                                for (i in document1.documents) {
//                                    val recentChat = i.toObject(RecentChats::class.java)
//                                    recentChat!!.messageId = i.id
//                                        recentChatList.add(recentChat)
//                                }
//
//                                fireStore.collection(Constants.RECENT_CHATS)
//                                    .whereEqualTo("receiver" , getCurrentUserID())
//                                    .get()
//                                    .addOnSuccessListener { querySnapshot ->
//                                        if(querySnapshot != null) {
//                                            for (document in querySnapshot) {
//                                                document.reference.collection("messages")
//                                                    .get()
//                                                    .addOnSuccessListener { document1 ->
//                                                        Log.e("User Application List", document1.documents.toString())
//                                                        for (i in document1.documents) {
//                                                            val recentChat = i.toObject(RecentChats::class.java)
//                                                            recentChat!!.messageId = i.id
//
//                                                            recentChatList.add(recentChat)
//                                                        }
//                                                        fragment.recentChatListLoaded(recentChatList)
//                                                    }
//
//                                            }
//                                        }
//                                    }
//
//                            }
//
//                    }
//                }
//            }
//
//    }
    fun getApplicationStatusList(activity: UserApplicationStatusListActivity) {
        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .whereEqualTo(Constants.APPLICANT_USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document1 ->
                Log.e("User Application List", document1.documents.toString())
                val applicationList: ArrayList<StrayAdoptionForm> = ArrayList()
                for (i in document1.documents) {
                    val application = i.toObject(StrayAdoptionForm::class.java)
                    application!!.applicationId = i.id

                    applicationList.add(application)
                }
                when (activity) {
                    is UserApplicationStatusListActivity -> {
                        activity.applicationListSuccessful(applicationList)
                    }
                }

            }
    }
    fun getPetUserApplicationsList(activity: Activity) {
        fireStore.collection(Constants.USER_PET_ADOPTION_FORM)
            .whereEqualTo(Constants.REVIEWER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e("User Pet Applicants List", document.documents.toString())

                val userPetApplicantsList: ArrayList<UserAdoptionForm> = ArrayList()
                for (i in document.documents)  {
                    val userPetApplicant = i.toObject(UserAdoptionForm::class.java)
                    userPetApplicant!!.applicationId = i.id

                    userPetApplicantsList.add(userPetApplicant)
                }
                when (activity) {
                    is ReviewUserPetApplicationActivity -> {
                        activity.userPetApplicantsListLoadedSuccessfullyFromFirestore(userPetApplicantsList)
                    }
                }
            }
    }

    fun getApplicationsList(activity: Activity) {
        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .whereNotEqualTo(Constants.REVIEW_STATUS, "Complete")
            .get()
            .addOnSuccessListener { document ->

                Log.e("Applicants List", document.documents.toString())

                val applicantsList: ArrayList<StrayAdoptionForm> = ArrayList()
                for (i in document.documents) {

                    val applicant = i.toObject(StrayAdoptionForm::class.java)
                    applicant!!.applicationId = i.id

                    applicantsList.add(applicant)
                }
                when (activity) {
                    is ReviewApplicationActivity -> {
                        activity.applicantsListLoadedSuccessfullyFromFirestore(applicantsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is ReviewApplicationActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Application List", "Error while getting applicant list.", e)

            }
    }

    fun getApprovedPetLists(fragment: Fragment) {
        fireStore.collection(Constants.PETS)
            .whereEqualTo(Constants.APPROVAL_STATUS, "Approved")
            .get()
            .addOnSuccessListener { document ->

                Log.e("Approved Pet Post Lists", document.documents.toString())
                val approvedPetList: ArrayList<Pet> = ArrayList()
                for (i in document.documents) {
                    val approvedPetPosts = i.toObject(Pet::class.java)
                    approvedPetPosts!!.petId = i.id

                    approvedPetList.add(approvedPetPosts)
                }
                when (fragment) {
                    is ApprovedPetPostsFragment -> {
                        fragment.approvedPetPostsLoaded(approvedPetList)
                    }

                }

            }
    }

    fun getDeclinedPetLists(fragment: Fragment) {
        fireStore.collection(Constants.PETS)
            .whereEqualTo(Constants.APPROVAL_STATUS, "Declined")
            .get()
            .addOnSuccessListener { document ->

                Log.e("Declined Pet Post Lists", document.documents.toString())
                val declinedPetList: ArrayList<Pet> = ArrayList()
                for (i in document.documents) {
                    val declinedPetPosts = i.toObject(Pet::class.java)
                    declinedPetPosts!!.petId = i.id

                    declinedPetList.add(declinedPetPosts)
                }
                when (fragment) {
                    is DeclinedPetPostsFragment -> {
                        fragment.declinedPetPostsLoaded(declinedPetList)
                    }

                }

            }
    }

    fun getPendingPetLists(fragment: Fragment) {
        fireStore.collection(Constants.PETS)
            .whereEqualTo(Constants.APPROVAL_STATUS, "Pending")
            .get()
            .addOnSuccessListener { document ->

                Log.e("Pending Pet Post Lists", document.documents.toString())
                val pendingPetList: ArrayList<Pet> = ArrayList()
                for (i in document.documents) {
                    val pendingPetPosts = i.toObject(Pet::class.java)
                    pendingPetPosts!!.petId = i.id

                    pendingPetList.add(pendingPetPosts)
                }
                when (fragment) {
                    is PendingPetPostsFragment -> {
                        fragment.pendingPetPostsLoaded(pendingPetList)
                    }

                }

            }
    }

    fun getPetsList(fragment: Fragment) {
        fireStore.collection(Constants.PETS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e("Pets List", document.documents.toString())

                val petsList: ArrayList<Pet> = ArrayList()
                for (i in document.documents) {

                    val pet = i.toObject(Pet::class.java)
                    pet!!.petId = i.id

                    petsList.add(pet)
                }

                when (fragment) {
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
                for (i in document.documents) {

                    val strayAnimal = i.toObject(StrayAnimal::class.java)
                    strayAnimal!!.strayAnimalId = i.id

                    strayAnimalList.add(strayAnimal)
                }

                when (fragment) {
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
    fun getUserPetApplicantDetails(activity: UserPetApplicationDetailsActivity, applicantId: String, petId: String) {
        fireStore.collection(Constants.USER_PET_ADOPTION_FORM)
            .document(applicantId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(javaClass.simpleName, document.toString())
                val userPetApplicant = document.toObject(UserAdoptionForm::class.java)
                if (userPetApplicant != null ) {
                    fireStore.collection(Constants.PETS).document(petId).get()
                        .addOnSuccessListener { document ->
                            Log.d(javaClass.simpleName, document.toString())
                            val pet = document.toObject(Pet::class.java)
                            if (pet != null) {
                                fireStore.collection(Constants.USERS)
                                    .document(userPetApplicant.userId!!)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        Log.d(javaClass.simpleName, document.toString())
                                        val otherUserDetails = document.toObject(User::class.java)

                                        activity.userPetApplicantDetailsSuccessfullyLoaded(userPetApplicant, pet, otherUserDetails!!)
                                    }

                            }

                        }
                }
            }
    }
    fun getApplicantDetails(activity: ApplicantDetailsActivity, applicantId: String, strayId: String) {

        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .document(applicantId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(javaClass.simpleName, document.toString())
                val applicant = document.toObject(StrayAdoptionForm::class.java)
                if (applicant != null) {
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

    fun getPostOwnerDetails(activity: Activity, petOwnerId: String) {
        fireStore.collection(Constants.USERS)
            .document(petOwnerId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val petOwner = document.toObject(User::class.java)
                if (petOwner != null) {
                    when (activity) {
                        is ViewPetPostDetailsActivity -> {
                            activity.postOwnerLoaded(petOwner)
                        }
                    }
                }
            }
    }

    fun getPendingPetDetails(activity: Activity, petId: String) {
        fireStore.collection(Constants.PETS)
            .document(petId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val pendingPet = document.toObject(Pet::class.java)
                if (pendingPet != null) {
                    when (activity) {
                        is ViewPetPostDetailsActivity -> {
                            activity.pendingPetLoaded(pendingPet)
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
            .addOnFailureListener { e ->
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
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the stray animal details",
                    e
                )
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
    fun query(fragment: Fragment, searchText: String, userList: ArrayList<User>, userAdapter: UserAdapter) {
        fireStore.collection(Constants.USERS)
            .orderBy(Constants.FIRST_NAME)
            .startAt(searchText)
            .endAt(searchText + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                userList.clear()
                for (document in snapshot.documents) {
                    val queriedData = document.toObject(User::class.java)
                    userList.add(queriedData!!)
                }
                userAdapter.notifyDataSetChanged()
            }
    }
    fun getUserList(fragment: SearchUserChatFragment) {
        fireStore.collection(Constants.USERS)
            .whereNotEqualTo("id", getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val userList: ArrayList<User> = ArrayList()

                for (i in document.documents) {
                    val user  = i.toObject(User::class.java)!!
                    user.userId = i.id
                    userList.add(user)
                }
                fragment.userListSuccessfullyLoadedToHome(userList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting user list", e)
            }
    }
    fun getPetsListToHome(fragment: UserHomeFragment) {
        fireStore.collection(Constants.PETS)
            .whereEqualTo(Constants.APPROVAL_STATUS, "Approved")
            .whereEqualTo(Constants.PET_ADOPTION_STATUS, "Listed")
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val petsList: ArrayList<Pet> = ArrayList()

                for (i in document.documents) {

                    val pet = i.toObject(Pet::class.java)!!
                    pet.petId = i.id
                    petsList.add(pet)
                }
                fragment.petListSuccessfullyLoadedToHome(petsList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting pets list", e)
            }

    }
    fun getUserPetApplicationStatus(fragment: PetApplicationStatusListFragment) {
        fireStore.collection(Constants.USER_PET_ADOPTION_FORM)
            .whereEqualTo(Constants.APPLICANT_USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val petUserApplicationList: ArrayList<UserAdoptionForm> = ArrayList()

                for (i in document.documents) {
                    val petUserApplication = i.toObject(UserAdoptionForm::class.java)
                    petUserApplication!!.applicationId = i.id
                    petUserApplicationList.add(petUserApplication)
                }
                fragment.userPetApplicationStatusListLoadedSuccessfully(petUserApplicationList)

            }
    }
    fun getUserStrayApplicationStatusList(fragment: StrayApplicationStatusListFragment) {
        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .whereEqualTo(Constants.APPLICANT_USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val strayUserApplicationList: ArrayList<StrayAdoptionForm> = ArrayList()

                for (i in document.documents) {
                    val strayUserApplication = i.toObject(StrayAdoptionForm::class.java)!!
                    strayUserApplication.applicationId = i.id
                    strayUserApplicationList.add(strayUserApplication)
                }
                fragment.userStrayApplicationStatusListLoadedSuccessfully(strayUserApplicationList)
            }
    }
    fun getPetFavoritesList(fragment: FavoritesPetFragment) {
        fireStore.collection(Constants.FAVORITES)
            .whereEqualTo(Constants.USER_ID_FAVORITES, getCurrentUserID())
            .whereEqualTo(Constants.CATEGORY, "userPet")
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val petFavoritesList: ArrayList<Favorites> = ArrayList()

                for (i in document.documents) {

                    val petFavorite = i.toObject(Favorites::class.java)!!
                    petFavorite.favoritesId = i.id
                    petFavoritesList.add(petFavorite)
                }
                fragment.petFavoritesLoadedSuccessfully(petFavoritesList)
            }


    }
    fun getStrayFavoritesList(fragment: FavoritesStrayFragment) {
        fireStore.collection(Constants.FAVORITES)
            .whereEqualTo(Constants.USER_ID_FAVORITES, getCurrentUserID())
            .whereEqualTo(Constants.CATEGORY, "stray")
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val strayFavoritesList: ArrayList<Favorites> = ArrayList()

                for (i in document.documents) {

                    val strayFavorite = i.toObject(Favorites::class.java)!!
                    strayFavorite.favoritesId = i.id
                    strayFavoritesList.add(strayFavorite)
                }
                fragment.strayFavoritesLoadedSuccessfully(strayFavoritesList)
            }


    }
    fun getStrayAnimalsListToHome(fragment: AdminHomeFragment) {
        fireStore.collection(Constants.STRAY_ANIMALS)
            .whereEqualTo(Constants.STRAY_ADOPTION_STATUS, "Listed")
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val strayAnimalsList: ArrayList<StrayAnimal> = ArrayList()

                for (i in document.documents) {

                    val strayAnimal = i.toObject(StrayAnimal::class.java)!!
                    strayAnimal.strayAnimalId = i.id
                    strayAnimalsList.add(strayAnimal)
                }
                fragment.strayAnimalListSuccessfullyLoadedToHome(strayAnimalsList)
            }
            .addOnFailureListener { e ->
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
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the pet details", e)

            }
    }

    fun changeApprovalStatus(
        activity: ViewPetPostDetailsActivity,
        postHashMap: HashMap<String, Any>,
        petId: String
    ) {
        fireStore.collection(Constants.PETS)
            .document(petId)
            .update(postHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is ViewPetPostDetailsActivity -> {
                        activity.reviewedPostSuccess()
                    }
                }
            }
    }
    fun changePetAdoptionStatus(activity: Activity, petHashMap: HashMap<String, Any>, petId: String) {
        fireStore.collection(Constants.PETS)
            .document(petId)
            .update(petHashMap)
            .addOnSuccessListener {
                when (activity)  {
                    is UserAdoptionActivity -> {
                        activity.changedPetAdoptionStatusSuccess()
                    }
                }
            }
    }
    fun changeStrayAdoptionStatus(activity: Activity, strayHashMap: HashMap<String, Any>, strayId: String) {
        fireStore.collection(Constants.STRAY_ANIMALS)
            .document(strayId)
            .update(strayHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is ApplicantDetailsActivity -> {
                        activity.changedStrayAdoptionStatus()
                    }
                    is StrayAdoptionActivity -> {
                        activity.changedStrayAdoptionStatusSuccess()
                    }

                }
            }
    }

    fun changeUserPetReviewStatus(activity: UserPetApplicationDetailsActivity, applicantHashMap: HashMap<String, Any>, applicantId: String) {
        fireStore.collection(Constants.USER_PET_ADOPTION_FORM)
            .document(applicantId)
            .update(applicantHashMap)
            .addOnSuccessListener {
                when(activity) {
                    is UserPetApplicationDetailsActivity -> {
                        activity.userPetReviewedApplicationSuccess()
                    }
                }
            }

    }
    fun changeReviewStatus(activity: ApplicantDetailsActivity, applicantHashMap: HashMap<String, Any>, applicantId: String
    ) {
        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .document(applicantId)
            .update(applicantHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is ApplicantDetailsActivity -> {
                        activity.reviewedApplicationSuccess()
                    }
                }
            }

    }

    fun getPetInfo(activity: PetUserApplicationStatusActivity, petId: String) {
        fireStore.collection(Constants.PETS)
            .document(petId)
            .get()
            .addOnSuccessListener { document ->
                if(document != null) {

                    Log.i(activity.javaClass.simpleName, document.toString())
                    val pet = document.toObject(Pet::class.java)
                    if(pet != null) {
                        activity.petInfoLoadedSuccessfully(pet)
                    }
                }
            }
    }
    fun getStrayInfo(activity: UserApplicationStatusActivity, petId: String) {
        fireStore.collection(Constants.STRAY_ANIMALS)
            .document(petId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    Log.i(activity.javaClass.simpleName, document.toString())
                    val stray = document.toObject(StrayAnimal::class.java)
                    if (stray != null) {
                        activity.strayInfoLoadedSuccessfully(stray)
                    }
                }
            }
    }

    fun getPetUserApplicationStatus(activity: PetUserApplicationStatusActivity, applicantId: String) {
        fireStore.collection(Constants.USER_PET_ADOPTION_FORM)
            .document(applicantId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    Log.i(activity.javaClass.simpleName, document.toString())
                    val applicant = document.toObject(UserAdoptionForm::class.java)
                    if(applicant != null) {
                        activity.applicationStatusLoaded(applicant)
                    }
                }
            }
    }
    fun getApplicationStatus(activity: UserApplicationStatusActivity, applicantId: String) {
        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .document(applicantId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    Log.i(activity.javaClass.simpleName, document.toString())
                    val applicant = document.toObject(StrayAdoptionForm::class.java)
                    if (applicant != null) {
                        activity.applicationStatusLoaded(applicant)
                    }
                }


            }


    }

    fun confirmPetUserAppointmentDate(activity: PetUserApplicationStatusActivity, appointmentHashMap: HashMap<String, Any>, applicantId: String) {
        fireStore.collection(Constants.USER_PET_ADOPTION_FORM)
            .document(applicantId)
            .update(appointmentHashMap)
            .addOnSuccessListener { document ->
                when (activity) {
                    is PetUserApplicationStatusActivity -> {
                        activity.confirmAppointmentDateSuccess()
                    }
                }
            }
    }
    fun confirmAppointmentDate(activity: UserApplicationStatusActivity, appointmentHashMap: HashMap<String, Any>, applicantId: String
    ) {
        fireStore.collection(Constants.STRAY_ANIMAL_ADOPTION_FORMS)
            .document(applicantId)
            .update(appointmentHashMap)
            .addOnCompleteListener { document ->
                when (activity) {
                    is UserApplicationStatusActivity -> {
                        activity.confirmAppointmentDateSuccess()
                    }
                }
            }
    }

    fun isAddedToPetFavorites(activity: Activity, petId: String) {
        fireStore.collection(Constants.FAVORITES)
            .whereEqualTo(Constants.USER_ID_FAVORITES, getCurrentUserID())
            .whereEqualTo(Constants.PET_ID_FAVORITES, petId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document != null) {
                        when (activity) {
                            is UserPetDetailsActivity -> {
                                activity.inFavorites()
                            }
                        }
                    }
                }
            }
    }

    fun isAddedToFavorites(activity: Activity, strayId: String) {
        fireStore.collection(Constants.FAVORITES)
            .whereEqualTo(Constants.USER_ID_FAVORITES, getCurrentUserID())
            .whereEqualTo(Constants.PET_ID_FAVORITES, strayId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document != null) {
                        when (activity) {
                            is StrayAnimalDetailsActivity -> {
                                activity.inFavorites()
                            }
                        }
                    }
                }
            }
    }

    fun favoritesListener(activity: Activity, strayId: String, favoritesId: String, category: String) {
        fireStore.collection(Constants.FAVORITES)
            .whereEqualTo(Constants.USER_ID_FAVORITES, getCurrentUserID())
            .whereEqualTo(Constants.PET_ID_FAVORITES, strayId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                when (activity) {
                                    is StrayAnimalDetailsActivity -> {
                                        activity.removeFromFavoritesSuccessfully()
                                    }
                                }
                            }

                    }
                } else {
                    fireStore.collection(Constants.STRAY_ANIMALS)
                        .document(strayId)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.i(activity.javaClass.simpleName, document.toString())
                                val strayAnimal = document.toObject(StrayAnimal::class.java)
                                if (strayAnimal != null) {
                                    val favorites = Favorites(
                                        strayId,
                                        getCurrentUserID(),
                                        category,
                                        strayAnimal.userId.toString(),
                                        strayAnimal.strayAnimalBreed.toString(),
                                        strayAnimal.strayAnimalBreed.toString(),
                                        strayAnimal.strayAnimalImage.toString()
                                    )
                                    fireStore.collection(Constants.FAVORITES)
                                        .document()
                                        .set(favorites, SetOptions.merge())
                                        .addOnSuccessListener {
                                            when (activity) {
                                                is StrayAnimalDetailsActivity -> {
                                                    activity.addedToFavoritesSuccessfully()
                                                }
                                            }
                                        }
                                }
                            }
                        }


                }

            }
        }
    fun petFavoritesListener(activity: Activity, petId: String, category: String) {
        fireStore.collection(Constants.FAVORITES)
            .whereEqualTo(Constants.USER_ID_FAVORITES, getCurrentUserID())
            .whereEqualTo(Constants.PET_ID_FAVORITES, petId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                when (activity) {
                                    is UserPetDetailsActivity -> {
                                        activity.removeFromFavoritesSuccessfully()
                                    }
                                }
                            }

                    }
                } else {
                    fireStore.collection(Constants.PETS)
                        .document(petId)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.i(activity.javaClass.simpleName, document.toString())
                                val pet = document.toObject(Pet::class.java)
                                if (pet != null) {
                                    val favorites = Favorites(
                                        petId,
                                        getCurrentUserID(),
                                        category,
                                        pet.userId.toString(),
                                        pet.petBreed.toString(),
                                        pet.petLocation.toString(),
                                        pet.image.toString(),
                                        pet.petName.toString()

                                    )
                                    fireStore.collection(Constants.FAVORITES)
                                        .document()
                                        .set(favorites, SetOptions.merge())
                                        .addOnSuccessListener {
                                            when (activity) {
                                                is UserPetDetailsActivity -> {
                                                    activity.addedToFavoritesSuccessfully()
                                                }
                                            }
                                        }
                                }
                            }
                        }

                }
            }
    }
}






