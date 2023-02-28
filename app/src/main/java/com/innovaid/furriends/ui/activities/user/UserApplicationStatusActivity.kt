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
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.admin.StrayAnimalDetailsActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_stray_animal_profile.*
import kotlinx.android.synthetic.main.activity_add_user_pet_profile.*
import kotlinx.android.synthetic.main.activity_set_up_user_profile.view.*
import kotlinx.android.synthetic.main.activity_user_application_status.*
import java.text.SimpleDateFormat
import java.util.*

class UserApplicationStatusActivity : BaseActivity(), View.OnClickListener {


    private var mPetId: String = ""
    private var mApplicantStatusId: String = ""
    private var mStrayAnimalOwnerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_application_status)



        if(intent.hasExtra(Constants.EXTRA_APPLICATION_ID)) {

            mApplicantStatusId = intent.getStringExtra(Constants.EXTRA_APPLICATION_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_STRAY_ID)) {

            mPetId = intent.getStringExtra(Constants.EXTRA_STRAY_ID)!!
        }


        btnSelectSchedule.setOnClickListener(this)
        btnSelectTime.setOnClickListener(this)
        btnConfirmAdoptionDate.setOnClickListener(this)
        ivViewPetProfile.setOnClickListener(this)
        setupActionBar()

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btnSelectSchedule -> {
                    setDate()
                }
                R.id.btnConfirmAdoptionDate -> {
                    confirmAppointmentDate()
                }
                R.id.ivViewPetProfile -> {
                    viewPetProfile()
                }
                R.id.btnSelectTime-> {
                    setTime()
                }
            }
        }
    }
    private fun viewPetProfile() {

        val intent = Intent(this, StrayAnimalDetailsActivity::class.java)
        intent.putExtra(Constants.EXTRA_STRAY_ANIMAL_ID,mPetId)
        intent.putExtra(Constants.EXTRA_STRAY_OWNER_ID,mStrayAnimalOwnerId)
        this.startActivity(intent)
    }
    private fun confirmAppointmentDate() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val appointmentTime = tvAppointmentTimeValue.text.toString().trim{ it <= ' '}
        val appointmentDate = tvAppointmentDateValue.text.toString().trim{ it <= ' '}
        val appointmentHashMap = HashMap<String, Any>()
        appointmentHashMap[Constants.APPOINTMENT_DATE] = "${appointmentDate} at ${appointmentTime}"
        FirestoreClass().confirmAppointmentDate(this, appointmentHashMap, mApplicantStatusId)
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
        tvAppointmentDateValue.setText(sdf.format(myCalendar.time))
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
                   tvApplicationMessage.text = "Application Approved! Set your Appointment"
                   clAppointmentScheduleContainer.visibility = View.VISIBLE
               } else {
                   tvStepTwoTextValue.text = "Visit the Veterinary at the scheduled date"
                   tvApplicationMessage.text = "Appointment Set! Visit the Veterinary for the interview on ${strayAdoptionForm.appointmentDate}"
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
        tbApplicationStatusActivity.setNavigationOnClickListener { onBackPressed() }
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

    override fun onResume() {
        super.onResume()
        applicationStatus()
    }

}