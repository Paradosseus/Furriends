package com.innovaid.furriends.ui.activities.admin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.innovaid.furriends.R
import com.innovaid.furriends.ReviewApplicationActivity
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_review_application.*
import kotlinx.android.synthetic.main.activity_view_stray_adoption_form.*

class ApplicantDetailsActivity :BaseActivity(), View.OnClickListener {

    private var mApplicantId: String =""
    private var mStrayId: String  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_details)


        if(intent.hasExtra(Constants.EXTRA_APPLICANT_USER_ID)) {
            mApplicantId = intent.getStringExtra(Constants.EXTRA_APPLICANT_USER_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_STRAY_ID)) {
            mStrayId = intent.getStringExtra(Constants.EXTRA_STRAY_ID)!!
        }

        btnApproveApplication.setOnClickListener(this)
        getApplicantDetails()

        setupActionBar()
    }

    fun getApplicantDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getApplicantDetails(this, mApplicantId, mStrayId)
    }
    fun applicantDetailSuccess(StrayAdoptionForm: StrayAdoptionForm, strayAnimal : StrayAnimal) {


        hideProgressDialog()
        GlideLoader(this).loadPetPicture(strayAnimal.strayAnimalImage!!, ivADStrayAnimalImage)
        tvADStrayBreed.text = strayAnimal.strayAnimalBreed
        tvADStrayGender.text = strayAnimal.strayAnimalGender
        tvADStrayColor.text = strayAnimal.strayAnimalColor
        tvADStrayLocation.text = strayAnimal.locationFounded
        tvADStrayDate.text = strayAnimal.dateFounded
        tvADStrayTime.text = strayAnimal.timeFounded
        tvADApplicantFormLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(StrayAdoptionForm.strayAnimalAdoptionForm)
            intent.setDataAndType(Uri.parse(StrayAdoptionForm.strayAnimalAdoptionForm), "application/pdf")
            startActivity(intent)
        }


        tvADApplicantName.text = StrayAdoptionForm.applicantName
        tvADApplicantAddress.text = StrayAdoptionForm.applicantAddress



    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.btnApproveApplication -> {
                   approveApplication()
                }
                R.id.btnDeclineApplication -> {
                    declineApplication()
                }
            }
        }
    }

    private fun approveApplication() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="approved_for_interview"
        FirestoreClass().approveApplication(this, applicantHashMap, mApplicantId)
    }
    fun reviewedApplicationSuccess() {
        finish()
    }
    private fun declineApplication() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="declined"
        FirestoreClass().declineApplication(this, applicantHashMap, mApplicantId)
    }


    private fun setupActionBar() {

        setSupportActionBar(tbApplicantDetailsActivity)
        supportActionBar!!.title = "Applicant Details"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbApplicantDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }


}