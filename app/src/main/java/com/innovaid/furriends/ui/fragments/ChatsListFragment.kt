package com.innovaid.furriends.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Chat
import com.innovaid.furriends.ui.adapters.ChatsListAdapter
import com.innovaid.furriends.ui.adapters.PetsListAdapter
import kotlinx.android.synthetic.main.fragment_chats_list.*
import kotlinx.android.synthetic.main.fragment_listings.*


class ChatsListFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats_list, container, false)
    }
    private fun getUserChatList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserChatList(this)
    }
    fun userChatListLoaded(chatList: ArrayList<Chat>) {
        hideProgressDialog()

        for(i in chatList) {
            Log.i("Pet Name", i.messageId!!)
        }
        if(chatList.size > 0) {
            rvChatList.visibility = View.VISIBLE
            tvNoChatList
                .visibility = View.GONE

            rvChatList.layoutManager = LinearLayoutManager(activity)
            rvChatList.setHasFixedSize(true)

            val adapterChatList = ChatsListAdapter(requireActivity(), chatList,this)
            rvChatList.adapter = adapterChatList
        } else {
            rvChatList.visibility = View.GONE
            tvNoChatList.visibility = View.VISIBLE
        }

    }
    }


