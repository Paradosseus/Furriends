package com.innovaid.furriends.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_pet_details.*
import kotlinx.android.synthetic.main.pet_list_layout.view.*

class PetDetailsActivity : BaseActivity() {

    private var mPetId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_details)

        if(intent.hasExtra(Constants.EXTRA_PET_ID)) {
            mPetId = intent.getStringExtra(Constants.EXTRA_PET_ID)!!
            Log.i("Pet id", mPetId)
        }
        getPetDetails()
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
}