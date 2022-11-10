package com.innovaid.furriends.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.innovaid.furriends.MainActivity
import com.innovaid.furriends.R

import com.innovaid.furriends.databinding.ActivityLoginBinding
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.User
import com.innovaid.furriends.utils.Constants

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }
    private fun loginUser() {

        val em = binding.etEmailAddress.text.toString().trim { it <= ' ' }
        val pass = binding.etPassword.text.toString().trim { it <= ' ' }

        if (em.isNotEmpty() && pass.isNotEmpty()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            firebaseAuth.signInWithEmailAndPassword(em, pass).addOnCompleteListener { task ->

                if(task.isSuccessful) {

                    FirestoreClass().getUserInfo(this)

                } else {
                    hideProgressDialog()
                    Toast.makeText(this,task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun userLoggedInSuccessful(user: User) {

        hideProgressDialog()

        if (user.profileCompleted == 0) {
            val intent = Intent(this, SetUpUserProfileActivity::class.java)
            intent.putExtra(Constants.OTHER_USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        finish()
    }
}