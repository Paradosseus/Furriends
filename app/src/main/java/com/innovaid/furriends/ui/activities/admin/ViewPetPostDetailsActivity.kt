package com.innovaid.furriends.ui.activities.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_user_application_status.*
import kotlinx.android.synthetic.main.activity_view_pet_post_details.*

class ViewPetPostDetailsActivity : BaseActivity() {

    private var mPetId: String = ""
    private var mPetOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pet_post_details)

        if(intent.hasExtra(Constants.EXTRA_PET_ID)) {
            mPetId = intent.getStringExtra(Constants.EXTRA_PET_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_PET_OWNER_ID)) {
            mPetOwnerId = intent.getStringExtra(Constants.EXTRA_PET_OWNER_ID)!!
        }

        setupActionBar()
        getPendingPetDetails()

        btnPostApprove.setOnClickListener {
            approvePost()
        }
        btnPostDecline.setOnClickListener {
            declinePost()
        }
    }
    private fun approvePost() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val postHashMap = HashMap<String, Any>()
        postHashMap[Constants.APPROVAL_STATUS] ="Approved"
        FirestoreClass().changeApprovalStatus(this, postHashMap, mPetId)
    }
    private fun  declinePost() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val postHashMap = HashMap<String, Any>()
        postHashMap[Constants.APPROVAL_STATUS] ="Declined"
        FirestoreClass().changeApprovalStatus(this, postHashMap, mPetId)
    }
    fun reviewedPostSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Post Reviewed", Toast.LENGTH_SHORT).show()
        finish()
    }
    private fun getPendingPetDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPendingPetDetails(this, mPetId)
        FirestoreClass().getPostOwnerDetails(this,mPetOwnerId)
    }
    fun pendingPetLoaded(pet: Pet) {
        hideProgressDialog()
        when(pet.approvalStatus) {
            "Pending" -> {
                llReviewButtonContainer.visibility = View.VISIBLE
            }
            "Approved" -> {
                llPetPostApprovalContainer.visibility = View.VISIBLE
                tvPetPostApprovalStatusValue.text = pet.approvalStatus
                tvPetPostApprovalStatusValue.setTextColor(ContextCompat.getColor(this, R.color.green))
            }
            "Declined" -> {
                llPetPostApprovalContainer.visibility = View.VISIBLE
                tvPetPostApprovalStatusValue.text = pet.approvalStatus
                tvPetPostApprovalStatusValue.setTextColor(ContextCompat.getColor(this, R.color.red))
            }
        }
        GlideLoader(this).loadPetPicture(pet.image!!, ivPetPostImage)
        tvPetPostNameValue.text = pet.petName
        tvPetPostBreedValue.text = pet.petBreed
        tvPetPostGenderValue.text = pet.petGender
        tvPetPostColorValue.text = pet.petColor


    }
    fun postOwnerLoaded(user: User) {
        GlideLoader(this).loadUserPicture(user.image, ivPetPostOwnerImage)
        tvPetPostOwnerNameValue.text = "${user.firstName} ${user.lastName}"
        tvPetPostOwnerAddressValue.text = user.address
        tvPetPostOwnerContactValue.text = user.phoneNumber.toString()
    }

    private fun setupActionBar() {

        setSupportActionBar(tbViewPetPostDetails)
        supportActionBar!!.title = "Manage Posts"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbViewPetPostDetails.setNavigationOnClickListener { onBackPressed() }
    }
}