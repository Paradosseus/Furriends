package com.innovaid.furriends.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.innovaid.furriends.R

import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.admin.AdminDashboardActivity
import com.innovaid.furriends.ui.activities.user.UserDashboardActivity
import com.innovaid.furriends.ui.activities.user.ForgotPasswordActivity
import com.innovaid.furriends.ui.activities.user.SetUpUserProfileActivity
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), View.OnClickListener {

    //Declares Firebase Authentication as Global Variable
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Getting the instance of Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener(this)

        btnCreateNewAccount.setOnClickListener(this)

        tvForgotPassword.setOnClickListener(this)

    }
    //Triggers specific code-block when given Ids are clicked
    override fun onClick(p0: View?) {
        if(p0 != null) {
            when (p0.id) {
                R.id.tvForgotPassword -> {
                    val intent = Intent(this, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.btnLogin -> {
                    loginUser()
                }
                R.id.btnCreateNewAccount -> {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    // A function that will login users with their registered email and password using Firebase Authentication
    private fun loginUser() {
        if (validateLoginCredentials()) {

            //Getting and storing the email and password into variables from the xml and trimming the space
            val em = etEmailAddress.text.toString().trim { it <= ' ' }
            val pass = etPassword.text.toString().trim { it <= ' ' }

            showProgressDialog(resources.getString(R.string.please_wait))
            //Login using Firebase Authentication
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
    //Validates if user has entered their login credentials or not
    private fun validateLoginCredentials(): Boolean {
        return when {
            TextUtils.isEmpty(etEmailAddress.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,"Please enter your email address", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(etPassword.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this,"Please enter your password", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }
    //A Function that notifies the user has logged in successfully and redirects them to the Dashboard Activity
    fun userLoggedInSuccessful(user: User) {

        hideProgressDialog()
        if (user.userType == "admin") {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        } else {
            if (user.profileCompleted == 0) {
                val intent = Intent(this, SetUpUserProfileActivity::class.java)
                intent.putExtra(Constants.OTHER_USER_DETAILS, user)
                startActivity(intent)
            } else {
                startActivity(Intent(this, UserDashboardActivity::class.java))
            }
        }
        finish()
    }

}