package com.innovaid.furriends

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(Constants.FURRIENDS_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        tvHello.text = "The logged in user is $username"

    }
}