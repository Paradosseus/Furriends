package com.innovaid.furriends

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.adapters.ApplicationListAdapter
import com.innovaid.furriends.ui.adapters.PetsListAdapter
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.activity_review_application.*
import kotlinx.android.synthetic.main.activity_stray_adoption.*
import kotlinx.android.synthetic.main.fragment_listings.*

class ReviewApplicationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_application)

        setupActionBar()


    }

    private fun getApplicantListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getApplicationsList(this)

    }
    fun applicantsListLoadedSuccessfullyFromFirestore(applicantList: ArrayList<StrayAdoptionForm>) {

        hideProgressDialog()

        for(i in applicantList) {
            Log.i("Applicant Name", i.applicantName!!)
        }
        if(applicantList.size > 0) {
            rvAdoptionApplicationsLists.visibility = View.VISIBLE
            tvNoApplicantListings.visibility = View.GONE

            rvAdoptionApplicationsLists.layoutManager = LinearLayoutManager(this)
            rvAdoptionApplicationsLists.setHasFixedSize(true)

            val applicationListAdapter = ApplicationListAdapter(this, applicantList)
            rvAdoptionApplicationsLists.adapter = applicationListAdapter
        } else {
            rvAdoptionApplicationsLists.visibility = View.GONE
            tvNoApplicantListings.visibility = View.VISIBLE
        }


    }

    private fun setupActionBar() {

        setSupportActionBar(tbReviewApplicationActivity)
        supportActionBar!!.title = "Review Applications"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbReviewApplicationActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        getApplicantListFromFireStore()
    }
}