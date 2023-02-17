package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.User
import com.innovaid.furriends.models.UserAdoptionForm
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_user_pet_application_details.*

class UserPetApplicationDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mApplicantId: String =""
    private var mPetId: String  = ""
    private var mApplicantName: String =""
    private var mApplicantUserId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pet_application_details)

        if(intent.hasExtra(Constants.EXTRA_APPLICANT_USER_ID)) {
            mApplicantId = intent.getStringExtra(Constants.EXTRA_APPLICANT_USER_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_PET_ID)) {
            mPetId = intent.getStringExtra(Constants.EXTRA_PET_ID)!!
        }

        getUserPetApplicantDetails()
        setupActionBar()

        btnUPApproveApplication.setOnClickListener(this)
        btnUPDeclineApplication.setOnClickListener(this)
        btnUPDoneInterview.setOnClickListener(this)
        btnUPPassedAssessment.setOnClickListener(this)
        btnUPFailedAssessment.setOnClickListener(this)
        btnUPPetClaimed.setOnClickListener(this)

    }
    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.btnUPApproveApplication -> {
                    approveApplication()
                }
                R.id.btnUPDeclineApplication -> {
                    declineApplication()
                }
                R.id.btnUPDoneInterview -> {
                    doneInterview()
                }
                R.id.btnUPPassedAssessment -> {
                    passedAssessment()
                }
                R.id.btnUPFailedAssessment -> {
                    failedAssessment()
                }
                R.id.btnUPPetClaimed -> {
                    petClaimed()
                }
            }
        }
    }


    private fun getUserPetApplicantDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserPetApplicantDetails(this, mApplicantId, mPetId)
    }


    fun userPetApplicantDetailsSuccessfullyLoaded(userPetApplicant: UserAdoptionForm, pet: Pet, otherUserDetails: User) {
        hideProgressDialog()

        when(userPetApplicant.reviewStatus) {
            "Application is being reviewed" -> {
                llUPBtnContainerReviewApplication.visibility = View.VISIBLE
            }
            "Approved for interview" -> {
                llUPContainerWaitingForAppointmentSchedule.visibility = View.VISIBLE
                if(userPetApplicant.appointmentDate != "none") {
                    llUPContainerWaitingForAppointmentSchedule.visibility = View.GONE
                    llUPContainerAppointmentScheduleDate.visibility = View.VISIBLE
                }
            }
            "Reviewing Assessment" -> {
                llUPBtnContainerReviewAssessment.visibility = View.VISIBLE
            }

            "Claim Pet"-> {
                llUPBtnContainerClaimPet.visibility = View.VISIBLE
            }
        }
        GlideLoader(this).loadPetPicture(pet.image!!, ivUPPetImage)
        tvUPPetName.text = pet.petName
        tvUPPetLocation.text = pet.petLocation
        tvUPPetBirthday.text = pet.petBirthDate


        tvUPApplicantName.text = userPetApplicant.applicantName
        tvUPApplicantAddress.text = userPetApplicant.applicantAddress
        tvUPApplicantContactNumber.text = "+63-${userPetApplicant.applicantContactNumber}"
        tvUPApplicantOccupation.text = userPetApplicant.applicantOccupation

        tvUPQuestionOneAnswer.text = userPetApplicant.questionOneAnswer
        tvUPQuestionTwoAnswer.text = userPetApplicant.questionTwoAnswer
        tvUPQuestionThreeAnswer.text = userPetApplicant.questionThreeAnswer
        tvUPQuestionFourAnswer.text = userPetApplicant.questionFourAnswer
        tvUPQuestionFiveAnswer.text = userPetApplicant.questionFiveAnswer

        ivUPApplicationStatusValue.text = userPetApplicant.reviewStatus

        tvUPAppointmentScheduleValue.text = userPetApplicant.appointmentDate

        mApplicantUserId = userPetApplicant.userId!!

        mApplicantName = userPetApplicant.applicantName!!

        GlideLoader(this).loadUserPicture(otherUserDetails.image!!, ivUPApplicantImage)
    }


    private fun doneInterview() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Reviewing Assessment"
        FirestoreClass().changeUserPetReviewStatus(this, applicantHashMap, mApplicantId)
    }

    private fun petClaimed() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val petHashMap = HashMap<String, Any>()
        petHashMap[Constants.ADOPTED_TO] = mApplicantName
        petHashMap[Constants.PET_ADOPTION_STATUS] = "Adopted"
        FirestoreClass().changePetAdoptionStatus(this,petHashMap, mPetId)

    }

    private fun failedAssessment() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Assessment Failed"
        FirestoreClass().changeUserPetReviewStatus(this, applicantHashMap, mApplicantId)
    }

    private fun passedAssessment() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Claim Pet"
        FirestoreClass().changeUserPetReviewStatus(this, applicantHashMap, mApplicantId)
    }

    private fun approveApplication() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Approved for interview"
        FirestoreClass().changeUserPetReviewStatus(this, applicantHashMap, mApplicantId)
    }
    fun changedPetAdoptionStatus() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Complete"
        FirestoreClass().changeUserPetReviewStatus(this, applicantHashMap, mApplicantId)
    }
    private fun declineApplication() {
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="Declined"
        FirestoreClass().changeUserPetReviewStatus(this, applicantHashMap, mApplicantId)
    }

    fun userPetReviewedApplicationSuccess() {
        hideProgressDialog()
        finish()
    }
    private fun setupActionBar() {

        setSupportActionBar(tbUserPetApplicantDetailsActivity)
        supportActionBar!!.title = "Applicant Details"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbUserPetApplicantDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

}