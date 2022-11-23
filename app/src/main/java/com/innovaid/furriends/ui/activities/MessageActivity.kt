package com.innovaid.furriends.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.innovaid.furriends.R
import com.innovaid.furriends.ui.activities.user.AddUserPetProfileActivity
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_stray_adoption.*

class MessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(tbMessageActivity)
        supportActionBar!!.title = "Messages"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbMessageActivity.setNavigationOnClickListener { onBackPressed() }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.add_send_message_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.actionSendMessage -> {
                Toast.makeText(this,"Message sent", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}