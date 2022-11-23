package com.innovaid.furriends.ui.activities.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.innovaid.furriends.databinding.ActivityForgotPasswordBinding
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_register.*

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.btnRPSubmit.setOnClickListener {
            val email = binding.etRPEmailAddress.text.toString().trim { it <= ' '}

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->

                        if(task.isSuccessful) {
                            Toast.makeText(this, "Email sent successfully to reset your password", Toast.LENGTH_SHORT).show()

                            finish()

                        }else {
                            Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        setupActionBar()

    }
    private fun setupActionBar() {

        setSupportActionBar(tbForgotPasswordActivity)
        supportActionBar!!.title = "Forgot Password?"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }

        tbForgotPasswordActivity.setNavigationOnClickListener { onBackPressed() }
    }
}
