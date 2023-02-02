package com.innovaid.furriends.ui.activities.user

import android.app.DatePickerDialog
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
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_user_pet_profile.*
import kotlinx.android.synthetic.main.activity_set_up_user_profile.view.*
import kotlinx.android.synthetic.main.activity_user_application_status.*
import java.text.SimpleDateFormat
import java.util.*

class UserApplicationStatusActivity : BaseActivity(), View.OnClickListener {


    private var mPetId: String = ""
    private var mApplicantStatusId: String = ""

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
        btnConfirmAdoptionDate.setOnClickListener(this)

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
            }
        }
    }

    private fun confirmAppointmentDate() {
        showProgressDialog(resources.getString(R.string.please_wait))

        val appointmentDate = tvAppointmentDateValue.text.toString().trim{ it <= ' '}
        val appointmentHashMap = HashMap<String, Any>()
        appointmentHashMap[Constants.APPOINTMENT_DATE] = appointmentDate
        FirestoreClass().confirmAppointmentDate(this, appointmentHashMap, mApplicantStatusId)
    }


    fun confirmAppointmentDateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Appointment Successful", Toast.LENGTH_SHORT).show()
        recreate()

    }

    private fun setDate() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateTable(myCalendar)
        }

        DatePickerDialog(
            this,
            datePicker,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateTable(myCalendar: Calendar) {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
        tvAppointmentDateValue.setText(sdf.format(myCalendar.time))
    }

    private fun applicationStatus() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getApplicationStatus(this,mApplicantStatusId)
        FirestoreClass().getPetInfo(this, mPetId)
    }
    fun petInfoLoadedSuccessfully (strayAnimal: StrayAnimal) {
        llToBeAdoptedPetInfoContainer.visibility = View.VISIBLE
        GlideLoader(this).loadPetPicture(strayAnimal.strayAnimalImage!!, ivToBeAdoptedPetImage)
        tvToBeAdoptedPetBreed.text = strayAnimal.strayAnimalBreed
        ivToBeAdoptedPetColor.text = strayAnimal.strayAnimalColor
        ivToBeAdoptedPetGender.text = strayAnimal.strayAnimalGender

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
               Complete()
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
        ivStepTwoIcon.setImageResource(R.drawable.done_step)
        tvStepThreeTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
        ivStepThreeIcon.setImageResource(R.drawable.step_three)
        tvApplicationMessage.text = "Your assessment is being reviewed"
    }
    private fun assessmentFailed() {
        ivStepOneIcon.setImageResource(R.drawable.done_step)
        tvStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivStepTwoIcon.setImageResource(R.drawable.done_step)
        tvStepThreeTextValue.setTextColor(ContextCompat.getColor(this, R.color.red))
        tvStepThreeTextValue.text = "You did not pass the interview"
        ivStepThreeIcon.setImageResource(R.drawable.step_decline)
        tvApplicationMessage.text = "You failed the assessment"
    }
    private fun claimPet() {
        ivStepOneIcon.setImageResource(R.drawable.done_step)
        tvStepOneTextValue.setTextColor(ContextCompat.getColor(this, R.color.gray))
        ivStepTwoIcon.setImageResource(R.drawable.done_step)
        ivStepThreeIcon.setImageResource(R.drawable.done_step)
        ivStepFourIcon.setImageResource(R.drawable.step_four)
        tvStepFourTextValue.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
        tvApplicationMessage.text = "You passed the assessment. Claim your pet "
    }
    private fun Complete() {

    }

    override fun onResume() {
        super.onResume()
        applicationStatus()
    }

}