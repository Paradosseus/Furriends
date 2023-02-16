package com.innovaid.furriends.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.innovaid.furriends.ui.fragments.user.FavoritesPetFragment
import com.innovaid.furriends.ui.fragments.user.FavoritesStrayFragment
import com.innovaid.furriends.ui.fragments.user.PetApplicationStatusListFragment
import com.innovaid.furriends.ui.fragments.user.StrayApplicationStatusListFragment

class ApplicationStatusViewPager(fm: FragmentManager, val fragmentCount: Int): FragmentStatePagerAdapter(fm) {

    private val fragmentTitleList = mutableListOf("City Pound", "Users")


    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return StrayApplicationStatusListFragment()
            1 -> return PetApplicationStatusListFragment()
            else -> return StrayApplicationStatusListFragment()
        }
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }

    override fun getCount(): Int = fragmentCount


}