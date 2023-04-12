package com.innovaid.furriends.ui.activities.user

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import android.widget.TimePicker
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.storage.internal.Util.parseDateTime
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.VideoCallActivity
import com.innovaid.furriends.ui.activities.admin.StrayAnimalDetailsActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_stray_animal_profile.*
import kotlinx.android.synthetic.main.activity_add_user_pet_profile.*
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_set_up_user_profile.view.*
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

class UserApplicationStatusActivity : BaseActivity(), View.OnClickListener {

    private lateinit var scheduledExecutorService: ScheduledExecutorService

    private var mPetId: String = ""
    private var mApplicantStatusId: String = ""
    private var mStrayAnimalOwnerId: String = ""
    private var mUserName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_application_status)



        if(intent.hasExtra(Constants.EXTRA_APPLICATION_ID)) {

            mApplicantStatusId = intent.getStringExtra(Constants.EXTRA_APPLICATION_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_STRAY_ID)) {

            mPetId = intent.getStringExtra(Constants.EXTRA_STRAY_ID)!!
        }
        if(intent.hasExtra(Constants.EXTRA_USER_NAME)) {

            mUserName = intent.getStringExtra(Constants.EXTRA_USER_NAME)!!
        }

        ivViewPetProfile.setOnClickListener(this)
        ibStrayUserApplicantJoinCall.setOnClickListener(this)
        setupActionBar()

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.ivViewPetProfile -> {
                    viewPetProfile()
                }
                R.id.ibStrayUserApplicantJoinCall ->{
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

        val intent = Intent(this, StrayAnimalDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_STRAY_ANIMAL_ID,mPetId)
        intent.putExtra(Constants.EXTRA_STRAY_OWNER_ID,mStrayAnimalOwnerId)
        this.startActivity(intent)
    }


    fun confirmAppointmentDateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Appointment Successful", Toast.LENGTH_SHORT).show()
        recreate()

    }


    private fun applicationStatus() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getApplicationStatus(this,mApplicantStatusId)
        FirestoreClass().getStrayInfo(this, mPetId)
    }
    fun strayInfoLoadedSuccessfully (strayAnimal: StrayAnimal) {
        llToBeAdoptedPetInfoContainer.visibility = View.VISIBLE
        GlideLoader(this).loadPetPicture(strayAnimal.strayAnimalImage!!, ivToBeAdoptedPetImage)
        tvToBeAdoptedPetBreed.text = strayAnimal.strayAnimalBreed
        ivToBeAdoptedPetColor.text = strayAnimal.strayAnimalColor
        ivToBeAdoptedPetGender.text = strayAnimal.strayAnimalGender

        mStrayAnimalOwnerId = strayAnimal.userId.toString()

    }
   fun applicationStatusLoaded(strayAdoptionForm: StrayAdoptionForm) {

       hideProgressDialog()

       llApplicationStepsContainer.visibility = View.VISIBLE

       when(strayAdoptionForm.reviewStatus) {
           "Application is being reviewed" -> {
               tvApplicationMessage.text = "Your application is being reviewed"
           }
           "Approved for interview" -> {
               approvedForInterview()

               if(strayAdoptionForm.appointmentDate == "none") {
                   tvApplicationMessage.text = "Application Approved! Wait for your schedule date of Interview"
                   tvStepTwoTextValue.text = "City Veterinary is setting the date for your interview"
               } else {
                   appointmentScheduleContainer.visibility = View.VISIBLE
                   tvStepTwoTextValue.text = "Attend the Virtual Interview at the scheduled date"
                   tvApplicationMessage.text = "Appointment Set! Prepare for the virtual interview on ${strayAdoptionForm.appointmentDate}. An icon will appear for the call"
                   val dateTimeString = strayAdoptionForm.appointmentDate!!
                   val regex = Regex("(\\d{2}-\\d{2}-\\d{4}) at (\\d{1,2}:\\d{2} [aApP][mM])")
                   val matchResult = regex.find(dateTimeString)
                   val dateString: String? = matchResult?.groupValues?.get(1)
                   val timeString: String? = matchResult?.groupValues?.get(2)
                   val appointSchedule = parseDateTime(dateString!!, timeString!!)
                   scheduleViewVisibility(clStrayUserApplicantVideoCallContainer, appointSchedule)
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
        setSupportActionBar(tbApplicationStatusActivity)
        supportActionBar!!.title = "Application Status"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbApplicationStatusActivity.setNavigationOnClickListener {
            val intent = Intent(this, UserDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
    private fun approvedForInterview() {
        ivStepOneIcon.setImageResource(R.drawable.done_step)
        tvStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivStepLineOne.setImageResource(R.drawable.line_reach)
        ivStepTwoIcon.setImageResource(R.drawable.step_two)
        tvStepTwoTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
    }
    private fun  applicationDeclined() {
        ivStepOneIcon.setImageResource(R.drawable.step_decline)
        tvStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.red))
        tvStepOneTextValue.text = "Your application was declined"
    }
    private fun reviewingAssessment() {
        ivStepOneIcon.setImageResource(R.drawable.done_step)
        tvStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivStepLineOne.setImageResource(R.drawable.line_reach)
        ivStepTwoIcon.setImageResource(R.drawable.done_step)
        tvStepThreeTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
        ivStepLineTwo.setImageResource(R.drawable.line_reach)
        ivStepThreeIcon.setImageResource(R.drawable.step_three)
        tvApplicationMessage.text = "Your assessment is being reviewed"
    }
    private fun assessmentFailed() {
        ivStepOneIcon.setImageResource(R.drawable.done_step)
        tvStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivStepLineOne.setImageResource(R.drawable.line_reach)
        ivStepTwoIcon.setImageResource(R.drawable.done_step)
        tvStepThreeTextValue.setTextColor(ContextCompat.getColor(this, R.color.red))
        tvStepThreeTextValue.text = "You did not pass the interview"
        ivStepLineTwo.setImageResource(R.drawable.line_reach)
        ivStepThreeIcon.setImageResource(R.drawable.step_decline)
        tvApplicationMessage.text = "You failed the assessment"
    }
    private fun claimPet() {
        ivStepOneIcon.setImageResource(R.drawable.done_step)
        tvStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivStepLineOne.setImageResource(R.drawable.line_reach)
        ivStepTwoIcon.setImageResource(R.drawable.done_step)
        ivStepLineTwo.setImageResource(R.drawable.line_reach)
        ivStepThreeIcon.setImageResource(R.drawable.done_step)
        ivStepLineThree.setImageResource(R.drawable.line_reach)
        ivStepFourIcon.setImageResource(R.drawable.step_four)
        tvStepFourTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
        tvApplicationMessage.text = "You passed the assessment. Claim your pet "
    }
    private fun adoptionComplete() {
        ivStepOneIcon.setImageResource(R.drawable.done_step)
        tvStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivStepLineOne.setImageResource(R.drawable.line_reach)
        ivStepTwoIcon.setImageResource(R.drawable.done_step)
        ivStepLineTwo.setImageResource(R.drawable.line_reach)
        ivStepThreeIcon.setImageResource(R.drawable.done_step)
        ivStepLineThree.setImageResource(R.drawable.line_reach)
        ivStepFourIcon.setImageResource(R.drawable.done_step)
        ivStepLineFour.setImageResource(R.drawable.line_reach)
        ivStepFiveIcon.setImageResource(R.drawable.done_step)
        tvStepFiveTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))

        tvApplicationMessage.text = "Application Complete! Take care of your newly-adopted furbaby "
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