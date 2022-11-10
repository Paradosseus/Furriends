package com.innovaid.furriends.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
import com.innovaid.furriends.R
import com.innovaid.furriends.viewmodel.HomeViewModel


class HomeFragment : Fragment() {
    //private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.tvHome)

            textView.text = "This is the Home Fragment"

        return root
    }

}