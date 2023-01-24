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
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.shuhart.stepview.StepView
import kotlinx.android.synthetic.main.activity_add_user_pet_profile.*
import kotlinx.android.synthetic.main.activity_user_application_status.*
import java.text.SimpleDateFormat
import java.util.*

class UserApplicationStatusActivity : BaseActivity(), View.OnClickListener {


    private var userId: String = ""
    private lateinit var mApplicant: StrayAdoptionForm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_application_status)

        if(intent.hasExtra(Constants.STRAY_ANIMAL_ADOPTION_FORMS)) {

            userId = intent.getStringExtra(Constants.STRAY_ANIMAL_ADOPTION_FORMS)!!
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
        FirestoreClass().confirmAppointmentDate(this, appointmentHashMap)
    }


    fun confirmAppointmentDateSuccess() {

        hideProgressDialog()
        Toast.makeText(this, "Appointment Successful", Toast.LENGTH_SHORT).show()

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
        FirestoreClass().getApplicationStatus(this)
    }

   fun applicationStatusLoaded(strayAdoptionForm: StrayAdoptionForm) {

       mApplicant = strayAdoptionForm
       svApplicationStatus.visibility = View.VISIBLE
       svApplicationStatus.setStepsNumber(4)


       hideProgressDialog()

       when(strayAdoptionForm.reviewStatus) {
           "application_being_reviewed" -> {
               tvApplicationMessage.text = "Your application is being reviewed"
           }
           "approved_for_interview" -> {
               if(strayAdoptionForm.appointmentDate == "none") {
                   tvApplicationMessage.text = "Application Approved! Set your Appointment"
                   clAppointmentScheduleContainer.visibility = View.VISIBLE
               } else {

                   tvApplicationMessage.text = "Appointment Set! Visit the Veterinary for the interview on ${strayAdoptionForm.appointmentDate}"
               }
           }
           "reviewing_assessment" -> {
               tvApplicationMessage.text = "Your assessment is being reviewed"
           }
           "claim_pet" -> {
               tvApplicationMessage.text = "You passed the assessment. Claim your pet "
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

    override fun onResume() {
        super.onResume()
        applicationStatus()
    }

}