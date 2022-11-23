package com.innovaid.furriends.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.innovaid.furriends.R
import com.innovaid.furriends.ui.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : Fragment() {

   private val myContext = FragmentActivity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureTopNavigation()
    }

    private fun configureTopNavigation() {
        vpItems.adapter = ViewPagerAdapter(childFragmentManager, 2)
        vpItems.offscreenPageLimit = 2
        tbDashboardFragment.setupWithViewPager(vpItems)
    }
}