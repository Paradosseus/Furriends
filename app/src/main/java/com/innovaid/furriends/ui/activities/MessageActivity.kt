package com.innovaid.furriends.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Chat
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.adapters.MessageAdapter
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_message.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageActivity : AppCompatActivity() {

    private var otherUserId: String = ""
    private var currentUser: String = ""
    private var otherUserImageUrl: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        if(intent.hasExtra(Constants.EXTRA_USER_ID)) {
            otherUserId = intent.getStringExtra(Constants.EXTRA_USER_ID)!!

        }
        currentUser = FirestoreClass().getCurrentUserID()


        ivSendMessageButton.setOnClickListener {
            val message = tvTextMessage.text.toString()
            if (message == null) {
                Toast.makeText(this,"Please write a message", Toast.LENGTH_SHORT).show()
            }
            else {
                sendMessageToUser(currentUser, otherUserId, message)
            }
            tvTextMessage.setText("")
        }
//        ivAttachImageButton.setOnClickListener {
//
//        }

        setupActionBar()
    }
    fun getChatId(senderId: String, receiverId: String): String {
        return if (senderId < receiverId) {
            "$senderId-$receiverId"
        } else {
            "$receiverId-$senderId"
        }
    }
    private fun generateTimestamp(): String {
        val current = Calendar.getInstance().time
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return format.format(current)
    }
    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {
        FirestoreClass().sendMessageToUser(senderId, receiverId, message)
    }
    private fun getOtherUserDetails() {
        FirestoreClass().getOtherUserDetails(this, otherUserId)
    }
    fun otherUserDetailsLoadedSuccess(user: User) {
        tvUsernameChat.text = "${user.firstName} ${user.lastName}"
        GlideLoader(this).loadUserPicture(user.image!!, ivOtherUserImage)
    }
    private fun getChatList() {
        FirestoreClass().getChatList(this, currentUser, otherUserId)
    }
    fun chatListLoadedSuccessfully(chatList: ArrayList<Chat>) {
        rvChats.layoutManager = LinearLayoutManager(this)
        rvChats.setHasFixedSize(true)

        val adapter = MessageAdapter(this, chatList, otherUserImageUrl)
        rvChats.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        getOtherUserDetails()
        getChatList()
    }

    private fun setupActionBar() {

        setSupportActionBar(tbMessagingActivity)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }

        tbMessagingActivity.setNavigationOnClickListener { onBackPressed() }
    }

}