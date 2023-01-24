package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.adapters.ApplicationListAdapter
import com.innovaid.furriends.ui.adapters.UserApplicationStatusListAdapter
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.activity_review_application.*
import kotlinx.android.synthetic.main.activity_user_application_status_list.*

class UserApplicationStatusListActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_application_status_list)

        setupActionBar()
    }


    private fun getApplicationStatusList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getApplicationStatusList(this)
    }
    fun applicationListSuccessful(applicationList: ArrayList<StrayAdoptionForm>) {
        hideProgressDialog()


        for(i in applicationList) {
            Log.i("Application", i.applicantName!!)
        }
        if(applicationList.size > 0) {
            rvUASLApplicationStatusList.visibility = View.VISIBLE
            tvUASLNoApplication.visibility = View.GONE

            rvUASLApplicationStatusList.layoutManager = LinearLayoutManager(this)
            rvUASLApplicationStatusList.setHasFixedSize(true)

            val applicationStrayListAdapter = UserApplicationStatusListAdapter(this, applicationList)
            rvUASLApplicationStatusList.adapter = applicationStrayListAdapter
        } else {
            rvUASLApplicationStatusList.visibility = View.GONE
            tvUASLNoApplication.visibility = View.VISIBLE
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(tbUserApplicationStatusListActivity)
        supportActionBar!!.title = "Application Status"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbUserApplicationStatusListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        getApplicationStatusList()
    }
}