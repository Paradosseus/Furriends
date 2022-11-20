package com.innovaid.furriends.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_pet_profile.*
import kotlinx.android.synthetic.main.activity_pet_details.*
import kotlinx.android.synthetic.main.pet_list_layout.view.*

class PetDetailsActivity : BaseActivity(),  View.OnClickListener {

    private var mPetId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_details)

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

        ibAddtoFavorite.setOnClickListener(this)
        ibPDBackButton.setOnClickListener(this)
    }
    fun getPetDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPetDetails(this, mPetId)
    }

    fun petDetailsSuccess(pet: Pet) {
        hideProgressDialog()
        GlideLoader(this@PetDetailsActivity).loadPetPicture(pet.image!!, ivPetDetailImage)
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
                R.id.ibAddtoFavorite -> {
                    ibAddtoFavorite.setImageResource(R.drawable.added_to_favorite_icon)
                }
                R.id.btnAdopt -> {

                }
                R.id.ibPDBackButton -> {
                    onBackPressed()
                }
            }
        }
    }
}