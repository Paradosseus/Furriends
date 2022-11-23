package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import kotlinx.android.synthetic.main.activity_user_application_status.*

class UserApplicationStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_application_status)

        setupActionBar()
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
}