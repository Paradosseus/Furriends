package com.innovaid.furriends

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.innovaid.furriends.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        //Initializing Navigation Drawer
        drawerLayout = binding.drawerLayout
        val navView : NavigationView = binding.navView


        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when(it.itemId) {

                R.id.homeFragment -> replaceFragment(HomeFragment(),it.title.toString())
                R.id.profileFragment -> replaceFragment(ProfileFragment(),it.title.toString())
                R.id.applicationStatusFragment -> replaceFragment(ApplicationStatusFragment(),it.title.toString())
                R.id.listingFragment -> replaceFragment(ListingsFragment(),it.title.toString())
                R.id.donateFragment -> replaceFragment(DonationFragment(),it.title.toString())
                R.id.logout -> Toast.makeText(applicationContext,"Clicked Logout", Toast.LENGTH_SHORT).show()
            }
            true
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.Home -> replaceFragment(HomeFragment(),it.title.toString())
                R.id.likedPets-> replaceFragment(LikedPetsFragment(),it.title.toString())
                R.id.notifications -> replaceFragment(NotificationFragment(),it.title.toString())
                R.id.inbox -> replaceFragment(InboxFragment(),it.title.toString())
            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment, title:String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}

