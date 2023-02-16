package com.innovaid.furriends.ui.activities.user

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateTable(myCalendar)
            setTime()
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
    private fun setTime() {
        tvDateTimeConnector.visibility = View.VISIBLE
        val currentTime  = Calendar.getInstance()
        val startHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentTime.get(Calendar.MINUTE)

        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            tvAppointmentTimeValue.setText("$hourOfDay: $minute")
        }, startHour, startMinute, false).show()
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