package com.innovaid.furriends.ui.activities.user

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.storage.internal.Util.parseDateTime
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.User
import com.innovaid.furriends.models.UserAdoptionForm
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.VideoCallActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_user_pet_application_details.*
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

class UserPetApplicationDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var scheduledExecutorService: ScheduledExecutorService

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
        btnUserDoneInterview.setOnClickListener(this)
        btnUPPassedAssessment.setOnClickListener(this)
        btnUPFailedAssessment.setOnClickListener(this)
        btnUPPetClaimed.setOnClickListener(this)
        ibPetUserApplicantStartCall.setOnClickListener(this)
        btnUserSelectDate.setOnClickListener(this)
        btnUserSelectTime.setOnClickListener(this)
        btnUserConfirmAdoptionDate.setOnClickListener(this)

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
                R.id.btnUserDoneInterview -> {
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
                R.id.ibPetUserApplicantStartCall -> {
                    makeCall()
                }
                R.id.btnUserSelectDate -> {
                    setDate()
                }
                R.id.btnUserSelectTime -> {
                    setTime()
                }
                R.id.btnUserConfirmAdoptionDate -> {
                    confirmUserAppointmentDate()
                }
            }
        }
    }
    private fun makeCall(){
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtra(Constants.EXTRA_APPLICATION_ID,mApplicantId)
        startActivity(intent)
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
                if(userPetApplicant.appointmentDate == "none") {
                    clUserAppointmentScheduleContainer.visibility = View.VISIBLE
                } else {
                    tvUserADApplicantAppointmentDateValue.visibility = View.VISIBLE
                    tvUserADApplicantAppointmentDateValue.text = "Appointment Date: ${userPetApplicant.appointmentDate}"
                    val dateTimeString = userPetApplicant.appointmentDate!!
                    val regex = Regex("(\\d{2}-\\d{2}-\\d{4}) at (\\d{1,2}:\\d{2} [aApP][mM])")
                    val matchResult = regex.find(dateTimeString)

                    val dateString: String? = matchResult?.groupValues?.get(1)
                    val timeString: String? = matchResult?.groupValues?.get(2)
                    val appointSchedule = parseDateTime(dateString!!, timeString!!)
                    scheduleViewVisibility(llUserCallContainer, appointSchedule)

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

        tvUPQuestionOneAnswer.text =
            userPetApplicant.questionOneAnswer
        tvUPQuestionTwoAnswer.text = userPetApplicant.questionTwoAnswer
        tvUPQuestionThreeAnswer.text = userPetApplicant.questionThreeAnswer
        tvUPQuestionFourAnswer.text = userPetApplicant.questionFourAnswer
        tvUPQuestionFiveAnswer.text = userPetApplicant.questionFiveAnswer

        ivUPApplicationStatusValue.text = userPetApplicant.reviewStatus

        mApplicantUserId = userPetApplicant.userId!!

        mApplicantName = userPetApplicant.applicantName!!

        GlideLoader(this).loadUserPicture(otherUserDetails.image!!, ivUPApplicantImage)
    }


    private fun setDate() {
        val myCalendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateTable(myCalendar)
        }
        val dialog = DatePickerDialog(
            this,
            datePicker,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.minDate = today.timeInMillis

        dialog.show()
    }

    private fun updateTable(myCalendar: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        tvUserAppointmentDateValue.setText(sdf.format(myCalendar.time))
    }

    private fun setTime() {
        val currentTime = Calendar.getInstance()
        val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentTime.get(Calendar.MINUTE)

        val timePickerView = TimePicker(this)
        timePickerView.setIs24HourView(false)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Time")
            .setView(timePickerView)
            .setPositiveButton("OK") { _, _ ->
                val hour = timePickerView.hour
                val minute = timePickerView.minute
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                tvUserAppointmentTimeValue.text = dateFormat.format(calendar.time)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // Set initial time
        timePickerView.hour = startHour
        timePickerView.minute = startMinute

        // Disable minutes and seconds
        try {
            val minuteSpinnerId = Resources.getSystem().getIdentifier("minute", "id", "android")
            val minuteSpinner = timePickerView.findViewById<View>(minuteSpinnerId)
            minuteSpinner?.visibility = View.GONE

            val secondSpinnerId = Resources.getSystem().getIdentifier("second", "id", "android")
            val secondSpinner = timePickerView.findViewById<View>(secondSpinnerId)
            secondSpinner?.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getStartTime(): Calendar {
        val startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.SECOND, 0)
        startTime.set(Calendar.MILLISECOND, 0)
        return startTime
    }

    private fun getEndTime(): Calendar {
        val endTime = Calendar.getInstance()
        endTime.set(Calendar.HOUR_OF_DAY, 23)
        endTime.set(Calendar.MINUTE, 59)
        endTime.set(Calendar.SECOND, 59)
        endTime.set(Calendar.MILLISECOND, 999)
        return endTime
    }

    private fun getNearestTime(time: Calendar): Calendar {
        val startTime = getStartTime()
        val endTime = getEndTime()
        return if (time.before(startTime)) {
            startTime
        } else if (time.after(endTime)) {
            endTime
        } else {
            time
        }
    }
    private fun confirmUserAppointmentDate() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val userAppointmentTime = tvUserAppointmentTimeValue.text.toString().trim{ it <= ' '}
        val userAppointmentDate = tvUserAppointmentDateValue.text.toString().trim{ it <= ' '}
        val appointmentHashMap = java.util.HashMap<String, Any>()
        appointmentHashMap[Constants.APPOINTMENT_DATE] = "${userAppointmentDate} at ${userAppointmentTime}"
        FirestoreClass().confirmUserAppointmentDate(this, appointmentHashMap, mApplicantId)
    }
    fun confirmUserAppointmentDateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Appointment Successful", Toast.LENGTH_SHORT).show()
        recreate()
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
        val petHashMap = HashMap<String, Any>()
        petHashMap[Constants.PET_ADOPTION_STATUS] = "Listed"
        FirestoreClass().declinedPetAdoptionStatus(this, petHashMap, mPetId)
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
        val petHashMap = HashMap<String, Any>()
        petHashMap[Constants.PET_ADOPTION_STATUS] = "Listed"
        FirestoreClass().declinedPetAdoptionStatus(this, petHashMap, mPetId)
        applicantHashMap[Constants.REVIEW_STATUS] ="Declined"
        FirestoreClass().changeUserPetReviewStatus(this, applicantHashMap, mApplicantId)
    }

    fun userPetReviewedApplicationSuccess() {
        hideProgressDialog()
        finish()
    }

    //Formats date and time value into one
    private fun parseDateTime(dateString: String, timeString: String): LocalDateTime {
        val dateTimeString = "${dateString.trim()} ${timeString.trim()}"
        val dateFormat = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MM-dd-yyyy h:mm a")
            .toFormatter(Locale.getDefault())
        return LocalDateTime.parse(dateTimeString, dateFormat)
    }
    //Function that makes the view visible
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