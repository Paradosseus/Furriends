package com.innovaid.furriends.ui.activities.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.innovaid.furriends.R
import com.innovaid.furriends.ViewStrayAdoptionFormActivity
import kotlinx.android.synthetic.main.activity_stray_adoption.*
import kotlinx.android.synthetic.main.activity_user_favorites.*

class StrayAdoptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stray_adoption)

        setupActionBar()

        val assetManager = this.assets
        val inputStream = assetManager.open("Adoption-Form-Sample.pdf")

        tvViewAdoptionForm.setOnClickListener {
            val intent = Intent(this, ViewStrayAdoptionFormActivity::class.java)
            startActivity(intent)
        }





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