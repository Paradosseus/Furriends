package com.innovaid.furriends.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import kotlinx.android.synthetic.main.fragment_chats_list.*
import kotlinx.android.synthetic.main.fragment_listings.*


class ChatsListFragment : BaseFragment() {

    private lateinit var mRootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_chats_list, container, false)
        return mRootView
    }
    private fun getUserChatList() {
//        showProgressDialog(resources.getString(R.string.please_wait))
//        FirestoreClass().addToRecentChats(this)
    }

    }


