package com.innovaid.furriends.ui.activities.admin

import android.os.Bundle
import android.util.Log
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.adapters.ManagePostsViewPageAdapter
import com.innovaid.furriends.ui.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_manage_posts.*
import kotlinx.android.synthetic.main.fragment_dashboard.*

class ManagePostsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_posts)

        setupActionBar()
        configureTopNavigation()
        
    }
    private fun configureTopNavigation() {
        vpManagePostItems.adapter = ManagePostsViewPageAdapter(supportFragmentManager, 3)
        vpManagePostItems.offscreenPageLimit = 3
        tlManagePosts.setupWithViewPager(vpManagePostItems)
    }

    
    private fun setupActionBar() {

        setSupportActionBar(tbManagePostsActivity)
        supportActionBar!!.title = "Manage Posts"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbManagePostsActivity.setNavigationOnClickListener { onBackPressed() }
    }



}