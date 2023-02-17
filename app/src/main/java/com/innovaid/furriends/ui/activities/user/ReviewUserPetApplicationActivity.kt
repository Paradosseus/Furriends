package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.UserAdoptionForm
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.adapters.reviewUserPetApplicationAdapter
import kotlinx.android.synthetic.main.activity_review_application.*
import kotlinx.android.synthetic.main.activity_review_user_pet_application.*

class ReviewUserPetApplicationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_user_pet_application)

        setupActionBar()
    }

    private fun getUserPetApplicantFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPetUserApplicationsList(this)
    }

    fun userPetApplicantsListLoadedSuccessfullyFromFirestore(userPetApplicantsList: ArrayList<UserAdoptionForm>) {
        hideProgressDialog()

        for(i in userPetApplicantsList) {
            Log.i("Applicant Name", i.applicantName!!)
        }
        if(userPetApplicantsList.size > 0) {
            rvUserPetApplicationsLists.visibility = View.VISIBLE
            tvNoUserPetApplicantListings.visibility = View.GONE

            rvUserPetApplicationsLists.layoutManager = LinearLayoutManager(this)
            rvUserPetApplicationsLists.setHasFixedSize(true)

            val userPetApplicationAdapter = reviewUserPetApplicationAdapter(this, userPetApplicantsList)
            rvUserPetApplicationsLists.adapter = userPetApplicationAdapter

        }
    }

    private fun setupActionBar() {

        setSupportActionBar(tbReviewUserPetApplicationActivity)
        supportActionBar!!.title = "Review Applications"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbReviewUserPetApplicationActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        getUserPetApplicantFromFireStore()
    }
}