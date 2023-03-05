package com.innovaid.furriends.ui.activities.admin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.innovaid.furriends.R
import com.innovaid.furriends.ReviewApplicationActivity
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.VideoCallActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_review_application.*
import kotlinx.android.synthetic.main.activity_view_stray_adoption_form.*

class ApplicantDetailsActivity :BaseActivity(), View.OnClickListener {

    private var mApplicantId: String =""
    private var mStrayId: String  = ""
    private var mApplicantName: String =""
    private var mApplicantUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_details)


        if(intent.hasExtra(Constants.EXTRA_APPLICATION_ID)) {
            mApplicantId = intent.getStringExtra(Constants.EXTRA_APPLICATION_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_STRAY_ID)) {
            mStrayId = intent.getStringExtra(Constants.EXTRA_STRAY_ID)!!
        }
        if(intent.hasExtra(Constants.EXTRA_APPLICANT_USER_ID)) {
            mApplicantUserId = intent.getStringExtra(Constants.EXTRA_APPLICANT_USER_ID)!!
        }



        getApplicantDetails()
        setupActionBar()


        btnApproveApplication.setOnClickListener(this)
        btnDeclineApplication.setOnClickListener(this)
        btnDoneInterview.setOnClickListener(this)
        btnPassedAssessment.setOnClickListener(this)
        btnFailedAssessment.setOnClickListener(this)
        btnPetClaimed.setOnClickListener(this)
        ibStrayUserApplicantStartCall.setOnClickListener(this)


    }


    private fun getApplicantDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getApplicantDetails(this, mApplicantId, mStrayId)
        FirestoreClass().getOtherUserDetails(this, mApplicantUserId)

    }
    fun userDetailLoadedSuccess(user: User) {
        GlideLoader(this).loadUserPicture(user.image!!, ivADApplicantImage)

    }

    fun applicantDetailSuccess(StrayAdoptionForm: StrayAdoptionForm, strayAnimal : StrayAnimal) {

        hideProgressDialog()

        when(StrayAdoptionForm.reviewStatus) {
            "Application is being reviewed" -> {
                llBtnContainerReviewApplication.visibility = View.VISIBLE
            }
            "Approved for interview" -> {
                llContainerWaitingForAppointmentSchedule.visibility = View.VISIBLE
                if(StrayAdoptionForm.appointmentDate != "none") {
                    llContainerWaitingForAppointmentSchedule.visibility = View.GONE
                    llContainerAppointmentScheduleDate.visibility = View.VISIBLE
                }
            }
            "Reviewing Assessment" -> {
                llBtnContainerReviewAssessment.visibility = View.VISIBLE
            }

            "Claim Pet"-> {
                llBtnContainerClaimPet.visibility = View.VISIBLE
            }
        }
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
//        GlideLoader(this).loadUserPicture(strayAnimal.strayAnimalImage!!, ivADApplicantImage)
        tvADApplicantName.text = StrayAdoptionForm.applicantName
        tvADApplicantAddress.text = StrayAdoptionForm.applicantAddress

        ivADApplicationStatusValue.text = StrayAdoptionForm.reviewStatus
        tvADAppointmentScheduleValue.text = StrayAdoptionForm.appointmentDate

        mApplicantName = StrayAdoptionForm.applicantName!!

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
                R.id.btnDoneInterview -> {
                    doneInterview()
                }
                R.id.btnPassedAssessment -> {
                    passedAssessment()
                }
                R.id.btnFailedAssessment -> {
                    FailedAssessment()
                }
                R.id.btnPetClaimed -> {
                    PetClaimed()
                }
                R.id.ibStrayUserApplicantStartCall -> {
                    makeCall()
                }
            }
        }
    }
    private fun makeCall(){
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtra(Constants.EXTRA_APPLICATION_ID,mApplicantId)
        startActivity(intent)
    }
    private fun doneInterview() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Reviewing Assessment"
        FirestoreClass().changeReviewStatus(this, applicantHashMap, mApplicantId)
    }

    private fun PetClaimed() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val strayHashMap = HashMap<String, Any>()
        strayHashMap[Constants.ADOPTED_TO] = mApplicantName
        strayHashMap[Constants.STRAY_ADOPTION_STATUS] = "Adopted"
        FirestoreClass().changeStrayAdoptionStatus(this,strayHashMap, mStrayId)


    }

    private fun FailedAssessment() {
        val applicantHashMap = HashMap<String, Any>()
        val strayHashMap = HashMap<String, Any>()
        strayHashMap[Constants.STRAY_ADOPTION_STATUS] = "Listed"
        FirestoreClass().declinedStrayAdoptionStatus(this,strayHashMap, mStrayId)
        applicantHashMap[Constants.REVIEW_STATUS] ="Assessment Failed"
        FirestoreClass().changeReviewStatus(this, applicantHashMap, mApplicantId)
    }

    private fun passedAssessment() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Claim Pet"
        FirestoreClass().changeReviewStatus(this, applicantHashMap, mApplicantId)
    }

    private fun approveApplication() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Approved for interview"
        FirestoreClass().changeReviewStatus(this, applicantHashMap, mApplicantId)
    }
    fun changedStrayAdoptionStatus() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Complete"
        FirestoreClass().changeReviewStatus(this, applicantHashMap, mApplicantId)
    }
    fun reviewedApplicationSuccess() {
        hideProgressDialog()
        finish()
    }
    private fun declineApplication() {
        val applicantHashMap = HashMap<String, Any>()
        val strayHashMap = HashMap<String, Any>()
        strayHashMap[Constants.STRAY_ADOPTION_STATUS] = "Listed"
        FirestoreClass().declinedStrayAdoptionStatus(this,strayHashMap, mStrayId)
        applicantHashMap[Constants.REVIEW_STATUS] ="Declined"
        FirestoreClass().changeReviewStatus(this, applicantHashMap, mApplicantId)
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