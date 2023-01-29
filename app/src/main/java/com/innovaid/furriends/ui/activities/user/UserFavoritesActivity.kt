package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.adapters.ManagePostsViewPageAdapter
import com.innovaid.furriends.ui.adapters.UserFavoritesViewPageAdapter
import kotlinx.android.synthetic.main.activity_manage_posts.*
import kotlinx.android.synthetic.main.activity_user_favorites.*

class UserFavoritesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_favorites)

        configureTopNavigation()
        setupActionBar()
    }

    private fun configureTopNavigation() {
        vpFavoritesItems.adapter = UserFavoritesViewPageAdapter(supportFragmentManager, 2)
        vpFavoritesItems.offscreenPageLimit = 2
        tlFavoritesTab.setupWithViewPager(vpFavoritesItems)
    }

    private fun setupActionBar() {

        setSupportActionBar(tbFavoritesActivity)
        supportActionBar!!.title = "Favorites"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbFavoritesActivity.setNavigationOnClickListener { onBackPressed() }
    }

}