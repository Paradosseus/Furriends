package com.innovaid.furriends.ui.activities.user

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.UserAdoptionForm
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.VideoCallActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_pet_user_application_status.*
import kotlinx.android.synthetic.main.activity_user_application_status.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class PetUserApplicationStatusActivity : BaseActivity(), View.OnClickListener {

    private lateinit var scheduledExecutorService: ScheduledExecutorService

    private var mPetId: String = ""
    private var mApplicantStatusId: String = ""
    private var mPetOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_user_application_status)

        if(intent.hasExtra(Constants.EXTRA_APPLICATION_ID)) {

            mApplicantStatusId = intent.getStringExtra(Constants.EXTRA_APPLICATION_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_PET_ID)) {

            mPetId = intent.getStringExtra(Constants.EXTRA_PET_ID)!!
        }


        ivPetUserViewPetProfile.setOnClickListener(this)
        ibPetUserApplicantJoinCall.setOnClickListener(this)
        setupActionBar()

    }



    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {

                R.id.ivPetUserViewPetProfile -> {
                    viewPetProfile()
                }
                R.id.ibPetUserApplicantJoinCall -> {
                    joinCall()
                }
            }
        }
    }
    private fun joinCall() {
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtra(Constants.EXTRA_APPLICATION_ID,mApplicantStatusId)
        startActivity(intent)
    }
    private fun viewPetProfile() {
        val intent = Intent(this, UserPetDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_PET_ID, mPetId)
        intent.putExtra(Constants.EXTRA_PET_OWNER_ID, mPetOwnerId)!!
        this.startActivity(intent)

    }

    fun confirmAppointmentDateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Appointment Successful", Toast.LENGTH_SHORT).show()
        recreate()
    }



    private fun applicationStatus() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPetUserApplicationStatus(this, mApplicantStatusId)
        FirestoreClass().getPetInfo(this, mPetId)
    }
    fun petInfoLoadedSuccessfully(pet: Pet) {
        llPetUserToBeAdoptedPetInfoContainer.visibility = View.VISIBLE
        GlideLoader(this).loadPetPicture(pet.image!!, ivPetUserToBeAdoptedPetImage)
        tvPetUserToBeAdoptedPetBreed.text = pet.petName
        ivPetUserToBeAdoptedPetColor.text = pet.petColor
        ivPetUSerToBeAdoptedPetGender.text = pet.petGender

        mPetOwnerId = pet.userId.toString()

    }

    fun applicationStatusLoaded(userAdoptionForm: UserAdoptionForm) {
        hideProgressDialog()

        llPetUserApplicationStepsContainer.visibility = View.VISIBLE

        when(userAdoptionForm.reviewStatus) {
            "Application is being reviewed" -> {
                tvPetUserApplicationMessage.text = "Your application is being reviewed"
            }
            "Approved for interview" -> {
                approvedForInterview()

                if(userAdoptionForm.appointmentDate == "none") {
                    tvPetUserApplicationMessage.text = "Application Approved! Set your Appointment"
                } else {
                    tvPetUserStepTwoTextValue.text = "Attend the Virtual Interview at the scheduled date"
                    tvPetUserApplicationMessage.text = "Appointment Set! Prepare for the virtual interview on ${userAdoptionForm.appointmentDate}. An icon will appear for the call"
                    val dateTimeString = userAdoptionForm.appointmentDate!!
                    val regex = Regex("(\\d{2}-\\d{2}-\\d{4}) at (\\d{1,2}:\\d{2} [aApP][mM])")
                    val matchResult = regex.find(dateTimeString)
                    val dateString: String? = matchResult?.groupValues?.get(1)
                    val timeString: String? = matchResult?.groupValues?.get(2)
                    val appointSchedule = parseDateTime(dateString!!, timeString!!)
                    scheduleViewVisibility(clPetUserApplicantVideoCallContainer, appointSchedule)
                }
            }
            "Declined" -> {
                applicationDeclined()
            }
            "Reviewing Assessment" -> {
                reviewingAssessment()

            }
            "Assessment Failed" -> {
                assessmentFailed()
            }
            "Claim Pet" -> {
                claimPet()
            }
            "Complete" -> {
                adoptionComplete()
            }
        }
    }
    private fun setupActionBar() {
        setSupportActionBar(tbPetUserApplicationStatusActivity)
        supportActionBar!!.title = "Application Status"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbPetUserApplicationStatusActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun approvedForInterview() {
        ivPetUserStepOneIcon.setImageResource(R.drawable.done_step)
        tvPetUserStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivPetUserStepLineOne.setImageResource(R.drawable.line_reach)
        ivPetUserStepTwoIcon.setImageResource(R.drawable.step_two)
        tvPetUserStepTwoTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
    }
    private fun  applicationDeclined() {
        ivPetUserStepOneIcon.setImageResource(R.drawable.step_decline)
        tvPetUserStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.red))
        tvPetUserStepOneTextValue.text = "Your application was declined"
    }
    private fun reviewingAssessment() {
        ivPetUserStepOneIcon.setImageResource(R.drawable.done_step)
        tvPetUserStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivPetUserStepLineOne.setImageResource(R.drawable.line_reach)
        ivPetUserStepTwoIcon.setImageResource(R.drawable.done_step)
        tvPetUserStepThreeTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
        ivPetUserStepLineTwo.setImageResource(R.drawable.line_reach)
        ivPetUserStepThreeIcon.setImageResource(R.drawable.step_three)
        tvPetUserApplicationMessage.text = "Your assessment is being reviewed"
    }
    private fun assessmentFailed() {
        ivPetUserStepOneIcon.setImageResource(R.drawable.done_step)
        tvPetUserStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivPetUserStepLineOne.setImageResource(R.drawable.line_reach)
        ivPetUserStepTwoIcon.setImageResource(R.drawable.done_step)
        tvPetUserStepThreeTextValue.setTextColor(ContextCompat.getColor(this, R.color.red))
        tvPetUserStepThreeTextValue.text = "You did not pass the interview"
        ivPetUserStepLineTwo.setImageResource(R.drawable.line_reach)
        ivPetUserStepThreeIcon.setImageResource(R.drawable.step_decline)
        tvPetUserApplicationMessage.text = "You failed the assessment"
    }
    private fun claimPet() {
        ivPetUserStepOneIcon.setImageResource(R.drawable.done_step)
        tvPetUserStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivPetUserStepLineOne.setImageResource(R.drawable.line_reach)
        ivPetUserStepTwoIcon.setImageResource(R.drawable.done_step)
        ivPetUserStepLineTwo.setImageResource(R.drawable.line_reach)
        ivPetUserStepThreeIcon.setImageResource(R.drawable.done_step)
        ivPetUserStepLineThree.setImageResource(R.drawable.line_reach)
        ivPetUserStepFourIcon.setImageResource(R.drawable.step_four)
        tvPetUserStepFourTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
        tvPetUserApplicationMessage.text = "You passed the assessment. Claim your pet "
    }
    private fun adoptionComplete() {
        ivPetUserStepOneIcon.setImageResource(R.drawable.done_step)
        tvPetUserStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivPetUserStepLineOne.setImageResource(R.drawable.line_reach)
        ivPetUserStepTwoIcon.setImageResource(R.drawable.done_step)
        ivPetUserStepLineTwo.setImageResource(R.drawable.line_reach)
        ivPetUserStepThreeIcon.setImageResource(R.drawable.done_step)
        ivPetUserStepLineThree.setImageResource(R.drawable.line_reach)
        ivPetUserStepFourIcon.setImageResource(R.drawable.done_step)
        ivPetUserStepLineFour.setImageResource(R.drawable.line_reach)
        ivPetUserStepFiveIcon.setImageResource(R.drawable.done_step)
        tvPetUserStepFiveTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))

        tvPetUserApplicationMessage.text = "Application Complete! Take care of your newly-adopted pet"
    }

    private fun parseDateTime(dateString: String, timeString: String): LocalDateTime {
        val dateTimeString = "${dateString.trim()} ${timeString.trim()}"
        val dateFormat = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MM-dd-yyyy h:mm a")
            .toFormatter(Locale.getDefault())
        return LocalDateTime.parse(dateTimeString, dateFormat)
    }
    private fun scheduleViewVisibility(view: View, scheduledDateTime: LocalDateTime) {
        val scheduledInstant = scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()
        val currentInstant = Instant.now()
        val delayMillis = Duration.between(currentInstant, scheduledInstant).toMillis()

        if (delayMillis > 0) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
            scheduledExecutorService.schedule({
                runOnUiThread {
                    view.visibility = View.VISIBLE
                }
            }, delayMillis, TimeUnit.MILLISECONDS)
        } else {
            view.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::scheduledExecutorService.isInitialized) {
            scheduledExecutorService.shutdown()
        }
    }

    override fun onResume() {
        super.onResume()
        applicationStatus()
    }
}