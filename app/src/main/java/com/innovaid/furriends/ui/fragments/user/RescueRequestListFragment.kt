package com.innovaid.furriends.ui.fragments.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.innovaid.furriends.R
import com.innovaid.furriends.ui.activities.user.RequestRescueActivity
import kotlinx.android.synthetic.main.fragment_rescue_request_list.*


class RescueRequestListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabRescueAStrayButton.setOnClickListener {
            val intent = Intent(context, RequestRescueActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rescue_request_list, container, false)
    }

}