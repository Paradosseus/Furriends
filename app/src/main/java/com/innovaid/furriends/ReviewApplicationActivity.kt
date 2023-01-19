package com.innovaid.furriends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_review_application.*
import kotlinx.android.synthetic.main.activity_stray_adoption.*

class ReviewApplicationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_application)


        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(tbReviewApplicationActivity)
        supportActionBar!!.title = "Review Applications"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbReviewApplicationActivity.setNavigationOnClickListener { onBackPressed() }
    }
}