package com.innovaid.furriends.ui.activities.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_check_appointment_list.*
import kotlinx.android.synthetic.main.activity_view_appointment_details.*

class ViewAppointmentDetailsActivity : BaseActivity() {

    private var mApplicantId: String =""
    private var mStrayId: String  = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_appointment_details)

        if(intent.hasExtra(Constants.EXTRA_APPLICANT_USER_ID)) {
            mApplicantId = intent.getStringExtra(Constants.EXTRA_APPLICANT_USER_ID)!!
        }

        if(intent.hasExtra(Constants.EXTRA_STRAY_ID)) {
            mStrayId = intent.getStringExtra(Constants.EXTRA_STRAY_ID)!!
        }

        getApppointmentDetails()

        setupActionBar()

        btnDoneInterview.setOnClickListener {
            doneInterview()

        }

    }
    private fun doneInterview() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val applicantHashMap = HashMap<String, Any>()
        applicantHashMap[Constants.REVIEW_STATUS] ="reviewing_assessment"
        FirestoreClass().doneInterview(this, applicantHashMap, mApplicantId)
    }
    fun doneInterviewSuccess() {
        hideProgressDialog()
        Toast.makeText(this,"Done Interview", Toast.LENGTH_SHORT).show()
    }

    fun getApppointmentDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getApppointmentDetails(this, mApplicantId, mStrayId)
    }
    fun appointmentDetailSuccess(strayAdoptionForm: StrayAdoptionForm, strayAnimal : StrayAnimal, user: User) {
        hideProgressDialog()
        GlideLoader(this).loadUserPicture(user.image!!, ivVADApplicantImage)
        tvVADApplicantName.text = strayAdoptionForm.applicantName
        tvVADApplicantAddress.text = strayAdoptionForm.applicantAddress
        tvVADDateOfAppointment.text = strayAdoptionForm.appointmentDate

        GlideLoader(this).loadPetPicture(strayAnimal.strayAnimalImage!!, ivVADStrayImage)
        tvVADStrayBreedValue.text = strayAnimal.strayAnimalBreed
        tvVADStrayGender.text = strayAnimal.strayAnimalGender
        tvVADStrayDate.text = strayAnimal.dateFounded
        tvVADStrayTime.text = strayAnimal.timeFounded
        tvVADStrayColorValue.text = strayAnimal.strayAnimalColor
        tvVADStrayLocation.text = strayAnimal.locationFounded

    }

    private fun setupActionBar() {

        setSupportActionBar(tbViewAppointmentDetails)
        supportActionBar!!.title = "Appointment Details"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbViewAppointmentDetails.setNavigationOnClickListener { onBackPressed() }
    }

}