package com.innovaid.furriends.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.innovaid.furriends.ui.fragments.ChatsListFragment
import com.innovaid.furriends.ui.fragments.SearchUserChatFragment
import com.innovaid.furriends.ui.fragments.admin.ApprovedPetPostsFragment
import com.innovaid.furriends.ui.fragments.admin.DeclinedPetPostsFragment
import com.innovaid.furriends.ui.fragments.admin.PendingPetPostsFragment

class InboxViewPagerAdapter (fm: FragmentManager, val fragmentCount: Int): FragmentStatePagerAdapter(fm) {

    private val fragmentTitleList = mutableListOf("Chats", "Search")


    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return ChatsListFragment()
            1 -> return SearchUserChatFragment()
            else -> return ChatsListFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }


    override fun getCount(): Int = fragmentCount


}