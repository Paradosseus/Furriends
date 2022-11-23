package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import kotlinx.android.synthetic.main.activity_user_favorites.*

class UserFavoritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_favorites)

        setupActionBar()
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