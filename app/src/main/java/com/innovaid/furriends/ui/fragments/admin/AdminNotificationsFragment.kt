package com.innovaid.furriends.ui.fragments.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.innovaid.furriends.R

class AdminNotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_admin_notifications, container, false)
        val textView: TextView = root.findViewById(R.id.tvAdminNotifications)
        textView.text = "You currently have no new notifications"
        return root

    }


}