package com.innovaid.furriends.ui.activities.user

import android.content.Intent
import android.os.Bundle
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : BaseActivity() {

    private lateinit var userDetails:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        setupActionBar()

        tvEditUserProfile.setOnClickListener {
            val intent = Intent(this, EditUserProfileActivity::class.java)
            intent.putExtra(Constants.OTHER_USER_DETAILS, userDetails)
            startActivity(intent)
        }

    }
    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserInfo(this)
    }
    fun userDetailsSuccess(user: User) {
        userDetails = user

        hideProgressDialog()

        GlideLoader(this).loadUserPicture(user.image, ivUserProfilePhoto)
        tvUserProfileName.text = "${user.firstName} ${user.lastName}"
        tvUserAddress.text = user.address
        tvUserEmail.text = user.email
        tvUserPhoneNumber.text = user.phoneNumber.toString()
        tvUserBio.text = user.bio
    }
    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
    private fun setupActionBar() {

        setSupportActionBar(tbProfileActivity)
        supportActionBar!!.title = "User Profile"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }


}