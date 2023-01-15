package com.innovaid.furriends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_application_form_lists.*
import kotlinx.android.synthetic.main.activity_user_adoption.*

class ApplicationFormListsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_form_lists)

    }
    private fun setupActionBar() {
        setSupportActionBar(tbApplicationFormListsActivity)
        supportActionBar!!.title = "Application Form Lists"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbApplicationFormListsActivity.setNavigationOnClickListener { onBackPressed() }
    }
}