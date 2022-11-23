package com.innovaid.furriends.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.innovaid.furriends.ui.fragments.admin.AdminHomeFragment
import com.innovaid.furriends.ui.fragments.user.UserHomeFragment

class ViewPagerAdapter(fm: FragmentManager, val fragmentCount: Int): FragmentStatePagerAdapter(fm){

    private val fragmentTitleList = mutableListOf("City Pound", "Users")

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return AdminHomeFragment()
            1 -> return UserHomeFragment()
            else -> return AdminHomeFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }
    override fun getCount(): Int = fragmentCount

}