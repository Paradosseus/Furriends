package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import kotlinx.android.synthetic.main.activity_user_adoption.*
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserAdoptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_adoption)

        setupActionBar()
    }
    private fun setupActionBar() {

        setSupportActionBar(tbUserAdoptionActivity)
        supportActionBar!!.title = "Adoption Form"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbUserAdoptionActivity.setNavigationOnClickListener { onBackPressed() }
    }
}