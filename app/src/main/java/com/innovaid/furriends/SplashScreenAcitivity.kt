package com.innovaid.furriends

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.google.firebase.auth.FirebaseAuth
import com.innovaid.furriends.databinding.ActivitySplashScreenAcitivityBinding
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.ui.activities.LoginActivity
import com.innovaid.furriends.ui.activities.user.SetUpUserProfileActivity
import com.innovaid.furriends.ui.activities.user.UserDashboardActivity
import com.innovaid.furriends.utils.Constants

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenAcitivityBinding
    private var FirebaseAuth: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenAcitivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.ivLogo.alpha = 0f
        binding.ivLogo.animate().setDuration(2000).alpha(1f).withEndAction {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }

    }

}