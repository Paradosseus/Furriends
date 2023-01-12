package com.innovaid.furriends.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.user.SetUpUserProfileActivity
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupActionBar()

        btnRCreateAccount.setOnClickListener {
            registerUser()
        }
        tvLogin.setOnClickListener {
            onBackPressed()
        }
    }
    //Sets up action bar
    private fun setupActionBar() {

        setSupportActionBar(tbRegisterActivity)
        supportActionBar!!.title = "Register"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }

        tbRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun registerUser() {

        showProgressDialog(resources.getString(R.string.please_wait))

        val userEmail = etREmailAddress.text.toString().trim { it <= ' ' }
        val userPassword = etRPassword.text.toString().trim { it <= ' ' }
        val confirmPassword = etRConfirmPassword.text.toString().trim { it <= ' ' }
        val userFirstName = etRFirstName.text.toString().trim { it <= ' ' }
        val userLastName = etRLastName.text.toString().trim { it <= ' ' }
        val userAccessLevel = "user"


        if (userEmail.isNotEmpty() && userPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if(userPassword == confirmPassword) {


                FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener { task ->


                    if (task.isSuccessful){

                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            userFirstName,
                            userLastName,
                            userEmail,
                            userAccessLevel

                        )

                        FirestoreClass().registerUser(this@RegisterActivity, user)

//                        val intent = Intent(this, DashboardActivity::class.java)
//                        startActivity(intent)

//                        FirebaseAuth.getInstance().signOut()
//                        finish()
                    }
                }
            }else {

                hideProgressDialog()

                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill up all the given fields", Toast.LENGTH_SHORT).show()
        }
    }

    fun userRegistrationSuccess() {

        hideProgressDialog()
        Toast.makeText(this, "You have registered successfully!", Toast.LENGTH_SHORT).show()
        FirestoreClass().getUserInfo(this)

    }
    fun userRegisteredSuccessful(user: User) {

        if (user.profileCompleted == 0) {
            val intent = Intent(this, SetUpUserProfileActivity::class.java)
            intent.putExtra(Constants.OTHER_USER_DETAILS, user)
            startActivity(intent)
        }
        finish()
    }


}