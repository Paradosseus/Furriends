package com.innovaid.furriends.ui.activities.user

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Toast
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Favorites
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.activities.BaseActivity
import com.innovaid.furriends.ui.activities.MessageActivity
import com.innovaid.furriends.ui.activities.admin.StrayAdoptionActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_pet_details.*

class UserPetDetailsActivity : BaseActivity(),  View.OnClickListener {

    private var mPetId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_pet_details)

        if(intent.hasExtra(Constants.EXTRA_PET_ID)) {
            mPetId = intent.getStringExtra(Constants.EXTRA_PET_ID)!!

        }
        var petOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_PET_OWNER_ID)) {
            petOwnerId = intent.getStringExtra(Constants.EXTRA_PET_OWNER_ID)!!
        }

        if(FirestoreClass().getCurrentUserID() == petOwnerId) {
            btnAdopt.visibility = View.GONE
            ibAddtoFavorite.visibility = View.GONE
            ibMessageUser.visibility = View.GONE
        } else{
            btnAdopt.visibility = View.VISIBLE
            ibAddtoFavorite.visibility = View.VISIBLE

            ibMessageUser.visibility = View.VISIBLE
        }
        getPetDetails()

        btnAdopt.setOnClickListener(this)
        ibAddtoFavorite.setOnClickListener(this)
        ibMessageUser.setOnClickListener(this)
        ibPDBackButton.setOnClickListener(this)
    }
    fun getPetDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPetDetails(this, mPetId)
    }

    fun petDetailsSuccess(pet: Pet) {
        hideProgressDialog()
        GlideLoader(this@UserPetDetailsActivity).loadPetPicture(pet.image!!, ivPetDetailImage)
        tvPetDetailBreed.text = pet.petBreed
        tvPetDetailName.text = pet.petName
        tvPetDetailGender.text = pet.petGender
        tvPetDetailColor.text = pet.petColor
        //Add Location
        tvPetDetailDescription.text = pet.description
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.btnAdopt -> {
                    startActivity(Intent(this, UserAdoptionActivity::class.java))
                }
                R.id.ibAddtoFavorite -> {
//                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
//                    ibAddtoFavorite.setImageResource(R.drawable.added_to_favorite_icon)
//                    addToFavorites()
                }
                R.id.ibMessageUser -> {
                    startActivity(Intent(this, MessageActivity::class.java))
                }
                R.id.ibPDBackButton -> {
                    onBackPressed()
                }
            }
        }
    }

//    private fun addToFavorites() {
//
//        val favorites = Favorites(
//            mPetId,
//            FirestoreClass().getCurrentUserID(),
//            true
//        )
//        FirestoreClass().addToFavorites(this, favorites)
//    }
    fun addedToFavorites() {
        ibAddtoFavorite.setImageResource(R.drawable.added_to_favorite_icon)
        Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
    }
}