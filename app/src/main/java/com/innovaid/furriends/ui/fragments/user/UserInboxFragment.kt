package com.innovaid.furriends.ui.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
//import androidx.lifecycle.ViewModelProviders
import com.innovaid.furriends.R


class UserInboxFragment : Fragment() {

    //private lateinit var inboxViewModel: InboxViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inboxViewModel = ViewModelProviders.of(this).get(InboxViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val textView: TextView = root.findViewById(R.id.tvNotifications)

            textView.text = "This is the Inbox Fragment"
        return root
    }


}