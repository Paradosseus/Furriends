package com.innovaid.furriends.ui.activities.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.innovaid.furriends.R
import kotlinx.android.synthetic.main.activity_stray_adoption.*
import kotlinx.android.synthetic.main.activity_user_favorites.*

class StrayAdoptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stray_adoption)

        setupActionBar()

        tvLinkToAdoptionForm.movementMethod = LinkMovementMethod.getInstance()
    }
    private fun setupActionBar() {

        setSupportActionBar(tbStrayAdoptionActivity)
        supportActionBar!!.title = "Adoption Form"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbStrayAdoptionActivity.setNavigationOnClickListener { onBackPressed() }
    }
}