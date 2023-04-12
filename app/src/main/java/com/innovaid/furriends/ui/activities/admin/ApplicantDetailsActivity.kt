package com.innovaid.furriends.ui.activities.admin

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import kotlinx.android.synthetic.main.activity_applicant_details.btnConfirmAdoptionDate
import kotlinx.android.synthetic.main.activity_applicant_details.btnSelectSchedule
import kotlinx.android.synthetic.main.activity_applicant_details.btnSelectTime
import kotlinx.android.synthetic.main.activity_review_application.*
import kotlinx.android.synthetic.main.activity_user_application_status.*
import kotlinx.android.synthetic.main.activity_view_stray_adoption_form.*
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class ApplicantDetailsActivity :BaseActivity(), View.OnClickListener {

    private lateinit var scheduledExecutorService: ScheduledExecutorService

    private var mApplicantId: String =""
    private var mStrayId: String  = ""
    private var mApplicantName: String =""
    private var mApplicantUserId: String = ""
    private var mAppointmentDate: String = ""
    private var mAppointmentTime: String = ""

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
        btnSelectSchedule.setOnClickListener(this)
        btnSelectTime.setOnClickListener(this)
        btnConfirmAdoptionDate.setOnClickListener(this)
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
                if (StrayAdoptionForm.appointmentDate == "none"){
                    clAppointmentScheduleContainer.visibility = View.VISIBLE
                } else {
                    tvADApplicantAppointmentDateValue.visibility = View.VISIBLE
                    tvADApplicantAppointmentDateValue.text = "Appointment Date: ${StrayAdoptionForm.appointmentDate}"
                    val dateTimeString = StrayAdoptionForm.appointmentDate!!
                    val regex = Regex("(\\d{2}-\\d{2}-\\d{4}) at (\\d{1,2}:\\d{2} [aApP][mM])")
                    val matchResult = regex.find(dateTimeString)

                    val dateString: String? = matchResult?.groupValues?.get(1)
                    val timeString: String? = matchResult?.groupValues?.get(2)
                    val appointSchedule = parseDateTime(dateString!!, timeString!!)
                    scheduleViewVisibility(llCallContainer, appointSchedule)
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
        tvADApplicantName.text = StrayAdoptionForm.applicantName
        tvADApplicantAddress.text = StrayAdoptionForm.applicantAddress

        ivADApplicationStatusValue.text = StrayAdoptionForm.reviewStatus

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
                R.id.btnSelectSchedule -> {
                    setDate()
                }
                R.id.btnSelectTime-> {
                    setTime()
                }
                R.id.btnConfirmAdoptionDate -> {
                    confirmAppointmentDate()
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
    //Set Date of Interview Schedule
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

        dialog.datePicker.init(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(
            Calendar.DAY_OF_MONTH)) { datePicker, year, monthOfYear, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(Calendar.YEAR, year)
            selectedCalendar.set(Calendar.MONTH, monthOfYear)
            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            if (selectedCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || selectedCalendar.get(
                    Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                Toast.makeText(this, "The City Pound is closed during weekends. Please select another day.", Toast.LENGTH_SHORT).show()
                // You can also prevent the user from selecting the date by setting it to the current date
                datePicker.updateDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(
                    Calendar.DAY_OF_MONTH))
            }
        }

        dialog.show()
    }
    private fun updateTable(myCalendar: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        tvAppointmentDateValue.setText(sdf.format(myCalendar.time))
    }
    //Set Time of Interview Schedule
    private fun setTime() {

        val currentTime = Calendar.getInstance()
        val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentTime.get(Calendar.MINUTE)

        val timePickerView = TimePicker(this)
        timePickerView.setOnTimeChangedListener { view, hourOfDay, minute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            if (selectedTime.before(getStartTime()) || selectedTime.after(getEndTime())) {
                // Selected time is outside the allowed range
                val nearestTime = getNearestTime(selectedTime)
                timePickerView.hour = nearestTime.get(Calendar.HOUR_OF_DAY)
                timePickerView.minute = nearestTime.get(Calendar.MINUTE)
                Toast.makeText(this, "Please select a time between 8:30 AM and 4:30 PM", Toast.LENGTH_SHORT).show()
            }
        }

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
                tvAppointmentTimeValue.text = dateFormat.format(calendar.time)
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

        // Set selectable range
        timePickerView.setIs24HourView(false)
    }

    private fun getStartTime(): Calendar {
        val startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, 8)
        startTime.set(Calendar.MINUTE, 30)
        startTime.set(Calendar.SECOND, 0)
        startTime.set(Calendar.MILLISECOND, 0)
        return startTime
    }

    private fun getEndTime(): Calendar {
        val endTime = Calendar.getInstance()
        endTime.set(Calendar.HOUR_OF_DAY, 16)
        endTime.set(Calendar.MINUTE, 30)
        endTime.set(Calendar.SECOND, 0)
        endTime.set(Calendar.MILLISECOND, 0)
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

    //Confirms Appointment Time and Date Schedule and Saves to Database
    private fun confirmAppointmentDate() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val appointmentTime = tvAppointmentTimeValue.text.toString().trim{ it <= ' '}
        val appointmentDate = tvAppointmentDateValue.text.toString().trim{ it <= ' '}
        val appointmentHashMap = java.util.HashMap<String, Any>()
        appointmentHashMap[Constants.APPOINTMENT_DATE] = "${appointmentDate} at ${appointmentTime}"
        FirestoreClass().confirmAppointmentDate(this, appointmentHashMap, mApplicantId)


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


    fun confirmAppointmentDateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Appointment Successful", Toast.LENGTH_SHORT).show()
        recreate()

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