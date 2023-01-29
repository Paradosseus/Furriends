package com.innovaid.furriends.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.innovaid.furriends.ui.fragments.admin.ApprovedPetPostsFragment
import com.innovaid.furriends.ui.fragments.admin.DeclinedPetPostsFragment
import com.innovaid.furriends.ui.fragments.admin.PendingPetPostsFragment
import com.innovaid.furriends.ui.fragments.user.FavoritesPetFragment
import com.innovaid.furriends.ui.fragments.user.FavoritesStrayFragment

class UserFavoritesViewPageAdapter(fm: FragmentManager, val fragmentCount: Int): FragmentStatePagerAdapter(fm)  {

    private val fragmentTitleList = mutableListOf("City Pound", "Users")

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return FavoritesStrayFragment()
            1 -> return FavoritesPetFragment()
            else -> return FavoritesStrayFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }

    override fun getCount(): Int = fragmentCount
}