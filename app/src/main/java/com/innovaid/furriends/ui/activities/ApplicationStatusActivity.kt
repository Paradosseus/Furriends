package com.innovaid.furriends.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import kotlinx.android.synthetic.main.activity_application_status.*

class ApplicationStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_status)

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