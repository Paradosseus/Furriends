package com.innovaid.furriends.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.innovaid.furriends.ui.fragments.admin.ApprovedPetPostsFragment
import com.innovaid.furriends.ui.fragments.admin.DeclinedPetPostsFragment
import com.innovaid.furriends.ui.fragments.admin.PendingPetPostsFragment

class ManagePostsViewPageAdapter(fm: FragmentManager, val fragmentCount: Int): FragmentStatePagerAdapter(fm) {

    private val fragmentTitleList = mutableListOf("Pending", "Approved", "Declined")



    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return PendingPetPostsFragment()
            1 -> return ApprovedPetPostsFragment()
            2 -> return DeclinedPetPostsFragment()
            else -> return PendingPetPostsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }

    override fun getCount(): Int = fragmentCount
}