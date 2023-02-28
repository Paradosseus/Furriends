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
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_pet_user_application_status.*
import kotlinx.android.synthetic.main.activity_user_application_status.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PetUserApplicationStatusActivity : BaseActivity(), View.OnClickListener {

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

        btnPetUserSelectSchedule.setOnClickListener(this)
        btnPetUserSelectTime.setOnClickListener(this)
        btnPetUserConfirmAdoptionDate.setOnClickListener(this)
        ivPetUserViewPetProfile.setOnClickListener(this)
        setupActionBar()

    }



    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.btnPetUserSelectSchedule -> {
                    setDate()
                }
                R.id.btnPetUserConfirmAdoptionDate -> {
                    confirmAppointmentDate()
                }
                R.id.ivPetUserViewPetProfile -> {
                    viewPetProfile()
                }
                R.id.btnSelectTime -> {
                    setTime()
                }
            }
        }
    }
    private fun viewPetProfile() {
        val intent = Intent(this, UserPetDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_PET_ID, mPetId)
        intent.putExtra(Constants.EXTRA_PET_OWNER_ID, mPetOwnerId)!!
        this.startActivity(intent)

    }
    private fun confirmAppointmentDate() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val appointmentTime = tvPetUserAppointmentTimeValue.text.toString().trim{ it <= ' '}
        val appointmentDate = tvPetUserAppointmentDateValue.text.toString().trim { it <= ' '}
        val appointmentHashMap = HashMap<String, Any>()
        appointmentHashMap[Constants.APPOINTMENT_DATE] = "${appointmentDate} at ${appointmentTime}"
        FirestoreClass().confirmPetUserAppointmentDate(this, appointmentHashMap, mApplicantStatusId)
    }
    fun confirmAppointmentDateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Appointment Successful", Toast.LENGTH_SHORT).show()
        recreate()
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

        dialog.datePicker.init(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)) { datePicker, year, monthOfYear, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(Calendar.YEAR, year)
            selectedCalendar.set(Calendar.MONTH, monthOfYear)
            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            if (selectedCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || selectedCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                Toast.makeText(this, "The City Pound is closed during weekends. Please select another day.", Toast.LENGTH_SHORT).show()
                // You can also prevent the user from selecting the date by setting it to the current date
                datePicker.updateDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
            }
        }

        dialog.show()
    }

    private fun updateTable(myCalendar: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        tvPetUserAppointmentDateValue.setText(sdf.format(myCalendar.time))
    }
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
                tvPetUserAppointmentTimeValue.text = dateFormat.format(calendar.time)
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
                    clPetUserAppointmentScheduleContainer.visibility = View.VISIBLE
                } else {
                    tvPetUserStepTwoTextValue.text = "Visit the Veterinary at the scheduled date"
                    tvPetUserApplicationMessage.text = "Appointment Set! Visit the Veterinary for the interview on ${userAdoptionForm.appointmentDate}"
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
    override fun onResume() {
        super.onResume()
        applicationStatus()
    }
}