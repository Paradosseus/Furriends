package com.innovaid.furriends.ui.activities.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.adapters.ApplicationListAdapter
import com.innovaid.furriends.ui.adapters.CheckAppointmentListAdapter
import kotlinx.android.synthetic.main.activity_check_appointment_list.*
import kotlinx.android.synthetic.main.activity_review_application.*

class CheckAppointmentListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_appointment_list)

        setupActionBar()
    }
    private fun getApplicantListFromFirestore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAppointmentList(this)

    }
    fun appointmentListLoadedSuccessfullyFromFirestore(appointmentList: ArrayList<StrayAdoptionForm>) {

        hideProgressDialog()

        for(i in appointmentList) {
            Log.i("Appointment", i.applicantName!!)
        }
        if(appointmentList.size > 0) {
            rvAdoptionAppointmentLists.visibility = View.VISIBLE
            tvNoAppointmentListings.visibility = View.GONE

            rvAdoptionAppointmentLists.layoutManager = LinearLayoutManager(this)
            rvAdoptionAppointmentLists.setHasFixedSize(true)

            val appointmentListAdapter = CheckAppointmentListAdapter(this, appointmentList)
            rvAdoptionAppointmentLists.adapter = appointmentListAdapter
        } else {
            rvAdoptionAppointmentLists.visibility = View.GONE
            tvNoAppointmentListings.visibility = View.VISIBLE
        }


    }

    private fun setupActionBar() {

        setSupportActionBar(tbCheckAppointmentList)
        supportActionBar!!.title = "Appointment List"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbCheckAppointmentList.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onResume() {
        super.onResume()
        getApplicantListFromFirestore()
    }
}