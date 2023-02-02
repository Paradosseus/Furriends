package com.innovaid.furriends.ui.fragments.user

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
//import androidx.lifecycle.ViewModelProviders
import com.innovaid.furriends.R
import com.innovaid.furriends.ui.adapters.InboxViewPagerAdapter
import com.innovaid.furriends.ui.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_inbox.*


class UserInboxFragment : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inboxViewModel = ViewModelProviders.of(this).get(InboxViewModel::class.java)
        return inflater.inflate(R.layout.fragment_inbox, container, false)

    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureTopNavigation()
    }


    private fun configureTopNavigation() {
        vpInboxItems.adapter = InboxViewPagerAdapter(childFragmentManager, 2)
        vpInboxItems.offscreenPageLimit = 2
        tbInboxFragment.setupWithViewPager(vpInboxItems)
    }

}