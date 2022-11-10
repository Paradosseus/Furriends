package com.innovaid.furriends.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.R
import kotlinx.android.synthetic.main.activity_listings.*

class ListingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listings)

        btnAddPet.setOnClickListener {
            startActivity(Intent(this,AddPetProfileActivity::class.java))
        }
    }
}