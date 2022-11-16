package com.innovaid.furriends.ui.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.User
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.side_nav_header.*

class ProfileActivity : BaseActivity() {

    private lateinit var userDetails:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupActionBar()

        tvEditUserProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra(Constants.OTHER_USER_DETAILS, userDetails)
            startActivity(intent)
        }

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
    override fun onResume() {
        super.onResume()
        getUserDetails()
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


}