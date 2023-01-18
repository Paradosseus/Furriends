package com.innovaid.furriends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.innovaid.furriends.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_stray_adoption.*
import kotlinx.android.synthetic.main.activity_view_stray_adoption_form.*

class ViewStrayAdoptionFormActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stray_adoption_form)

        val assetManager = this.assets
        val inputStream = assetManager.open("Adoption-Form-Interactive.pdf")

        val adoptionFormView = pvStrayAdoptionFormView
        adoptionFormView.fromStream(inputStream).load()
        setupActionBar()

    }

    private fun setupActionBar() {

        setSupportActionBar(tbViewStrayAdoptionFormActivity)
        supportActionBar!!.title = "Adoption Form"
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)

        }
        tbViewStrayAdoptionFormActivity.setNavigationOnClickListener { onBackPressed() }
    }
}