package com.innovaid.furriends.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import kotlinx.android.synthetic.main.activity_donate.*
import kotlinx.android.synthetic.main.activity_favorites.*

class DonateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        setupActionBar()
    }
    private fun setupActionBar() {

        setSupportActionBar(tbDonateActivity)
        supportActionBar!!.title = "Donate"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbDonateActivity.setNavigationOnClickListener { onBackPressed() }
    }
}