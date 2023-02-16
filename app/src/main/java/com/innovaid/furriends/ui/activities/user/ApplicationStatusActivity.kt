package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.adapters.ApplicationStatusViewPager
import com.innovaid.furriends.ui.adapters.UserFavoritesViewPageAdapter
import kotlinx.android.synthetic.main.activity_application_status.*
import kotlinx.android.synthetic.main.activity_user_favorites.*

class ApplicationStatusActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_status)

        configureTopNavigation()
        setupActionBar()
    }


    private fun configureTopNavigation() {
        vpApplicationStatusItems.adapter = ApplicationStatusViewPager(supportFragmentManager, 2)
        vpApplicationStatusItems.offscreenPageLimit = 2
        tlApplicationStatusTab.setupWithViewPager(vpApplicationStatusItems)
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