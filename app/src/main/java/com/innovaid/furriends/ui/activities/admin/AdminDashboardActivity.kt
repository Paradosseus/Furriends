package com.innovaid.furriends.ui.activities.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.innovaid.furriends.R
import com.innovaid.furriends.ReviewApplicationActivity
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.DonateActivity
import com.innovaid.furriends.ui.activities.LoginActivity
import com.innovaid.furriends.ui.activities.user.UserApplicationStatusActivity
import com.innovaid.furriends.ui.activities.user.UserFavoritesActivity
import com.innovaid.furriends.ui.activities.user.UserProfileActivity
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_admin_dashboard.*
import kotlinx.android.synthetic.main.activity_user_dashboard.*
import kotlinx.android.synthetic.main.side_nav_header.*

class AdminDashboardActivity : BaseActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private lateinit var userDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        drawerLayout = findViewById(R.id.adminDrawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout,R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adminSideNavigationMenu.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_admin_profile -> startActivity(Intent(this, UserProfileActivity::class.java))
                R.id.nav_admin_application_status -> startActivity(Intent(this, ManagePostsActivity::class.java))
                R.id.nav_admin_favorites -> startActivity(Intent(this, UserFavoritesActivity::class.java))
                R.id.nav_review_applications -> startActivity(Intent(this,ReviewApplicationActivity::class.java))
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

        //Bottom Navigation Menu
        val bottomNavView: BottomNavigationView = findViewById(R.id.adminBottomNavigationView)
        val navController = findNavController(R.id.admin_nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_dashboard,
                R.id.nav_admin_stray_listings,
                R.id.nav_admin_notifications,
                R.id.nav_admin_inbox
            ),drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)

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

        GlideLoader(this).loadUserPicture(user.image, ivHeaderUserImage)
        tvHeaderUserName.text = "${user.firstName} ${user.lastName}"

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        doubleBackToExit()
    }

}