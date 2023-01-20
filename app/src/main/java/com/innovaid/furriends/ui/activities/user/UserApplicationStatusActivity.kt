package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.User
import com.innovaid.furriends.utils.Constants
import com.shuhart.stepview.StepView
import kotlinx.android.synthetic.main.activity_user_application_status.*

class UserApplicationStatusActivity : AppCompatActivity() {

    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_application_status)

        if(intent.hasExtra(Constants.STRAY_ANIMAL_ADOPTION_FORMS)) {

            userId = intent.getStringExtra(Constants.STRAY_ANIMAL_ADOPTION_FORMS)!!
        }

        setupActionBar()


        svApplicationStatus.setStepsNumber(5)
        val steps = listOf("Application is being reviewed", "Schedule an Appointment", "Assessment is being reviewed","Claim your pet", "Done")
        svApplicationStatus.setSteps(steps)



    }

    private fun applicationStatus() {
        FirestoreClass().getApplicationStatus(this)
    }

   fun applicationStatusLoaded(strayAdoptionForm: StrayAdoptionForm) {


        if(strayAdoptionForm.reviewStatus == "application_being_reviewed") {
            svApplicationStatus.visibility = View.VISIBLE
            tvApplicationMessage.text = "Your application is being reviewed"
        }
       if(strayAdoptionForm.reviewStatus == "approved_for_interview") {
                svApplicationStatus.visibility = View.VISIBLE
                svApplicationStatus.go(1,true)
                tvApplicationMessage.text = "Your application is approved! Set your appointment!"
            }
       if(strayAdoptionForm.reviewStatus == "reviewing_assessment") {
           svApplicationStatus.visibility = View.VISIBLE
           svApplicationStatus.go(2, true)
           tvApplicationMessage.text = "Your assessment is being reviewed"
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