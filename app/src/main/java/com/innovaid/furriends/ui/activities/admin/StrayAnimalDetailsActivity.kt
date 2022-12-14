package com.innovaid.furriends.ui.activities.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.MessageActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_stray_animal_profile.*
import kotlinx.android.synthetic.main.activity_set_up_user_profile.*
import kotlinx.android.synthetic.main.activity_stray_animal_details.*
import kotlinx.android.synthetic.main.activity_user_pet_details.*

class StrayAnimalDetailsActivity : BaseActivity(), View.OnClickListener {

    private var mStrayAnimalId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stray_animal_details)

        if(intent.hasExtra(Constants.EXTRA_STRAY_ANIMAL_ID)) {
            mStrayAnimalId = intent.getStringExtra(Constants.EXTRA_STRAY_ANIMAL_ID)!!
        }
        var strayAnimalOwnerId: String =""

        if (intent.hasExtra(Constants.EXTRA_STRAY_OWNER_ID)) {
            strayAnimalOwnerId = intent.getStringExtra(Constants.EXTRA_STRAY_OWNER_ID)!!
        }
        if(FirestoreClass().getCurrentUserID() == strayAnimalOwnerId) {
            btnAdoptStrayAnimal.visibility = View.GONE
            ibAddStrayAnimalToFavorite.visibility = View.GONE
            ibMessageAdmin.visibility = View.GONE
        } else{
            btnAdoptStrayAnimal.visibility = View.VISIBLE
            ibAddStrayAnimalToFavorite.visibility = View.VISIBLE
            ibMessageAdmin.visibility = View.VISIBLE

        }

        getStrayAnimalDetails()

        btnAdoptStrayAnimal.setOnClickListener(this)
        ibAddStrayAnimalToFavorite.setOnClickListener(this)
        ibMessageAdmin.setOnClickListener(this)
        ibSADBackButton.setOnClickListener(this)

    }
    fun getStrayAnimalDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getStrayAnimalDetails(this, mStrayAnimalId)
    }
    fun strayAnimalSuccess(strayAnimal: StrayAnimal) {
        hideProgressDialog()
        GlideLoader(this@StrayAnimalDetailsActivity).loadPetPicture(strayAnimal.strayAnimalImage!!, ivStrayAnimalDetailImage)
        tvStrayAnimalDetailBreed.text = strayAnimal.strayAnimalBreed
        tvStrayAnimalDetailGender.text = strayAnimal.strayAnimalGender
        tvStrayAnimalDetailColor.text = strayAnimal.strayAnimalColor
        tvStrayAnimalDetailLocationFoundedValue.text = strayAnimal.locationFounded
        tvStrayAnimalDetailDateAndTimeFounded.text = "${strayAnimal.dateFounded} at ${strayAnimal.timeFounded}"
        tvStrayAnimalDetailDescription.text = strayAnimal.strayAnimalDescription



    }
    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.btnAdoptStrayAnimal -> {
                    startActivity(Intent(this, StrayAdoptionActivity::class.java))
                }
                R.id.ibAddStrayAnimalToFavorite -> {
                    ibAddStrayAnimalToFavorite.setImageResource(R.drawable.added_to_favorite_icon)
                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
                }
                R.id.ibMessageAdmin -> {
                    startActivity(Intent(this, MessageActivity::class.java))
                }
                R.id.ibSADBackButton -> {
                    onBackPressed()
                }
            }
        }
    }
}