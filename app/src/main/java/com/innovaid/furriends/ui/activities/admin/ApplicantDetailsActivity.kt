package com.innovaid.furriends.ui.activities.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_review_application.*

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

        tvADApplicantName.text = StrayAdoptionForm.applicantName
        tvADApplicantAddress.text = StrayAdoptionForm.applicantAddress


    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
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