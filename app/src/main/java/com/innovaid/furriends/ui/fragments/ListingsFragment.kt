package com.innovaid.furriends.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.activities.AddPetProfileActivity
import com.innovaid.furriends.ui.activities.EditPetProfileActivity
import com.innovaid.furriends.ui.activities.EditProfileActivity
import com.innovaid.furriends.ui.adapters.PetsListAdapter
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.fragment_listings.*

class ListingsFragment : BaseFragment() {

    private lateinit var mRootView: View
    private lateinit var petDetails: Pet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.fragment_listings,container, false)
        return mRootView
    }
    fun editPetProfile() {
        val intent = Intent(requireActivity(), EditPetProfileActivity::class.java)
//        intent.putExtra(Constants.OTHER_PET_DETAILS,petDetails)
        startActivity(intent)
    }
    fun deletePet(petID: String) {
        showAlertDialogToDeletePetProfile(petID)
    }
    private fun showAlertDialogToDeletePetProfile(petID: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Delete")
        builder.setMessage("Are you sure you want to delete?")
        //Add Icon

        builder.setPositiveButton("Yes") {
            dialogInterface, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deletePet(this, petID)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") {
                dialogInterface, _ ->

            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    fun deletePetSuccess() {
        hideProgressDialog()
        Toast.makeText(requireActivity(), "Successfully Delete the Product", Toast.LENGTH_SHORT).show()

        getPetsListFromFireStore()
    }

    fun petListLoadedSuccessfullyFromFireStore(petsList: ArrayList<Pet>) {

        hideProgressDialog()

        for(i in petsList) {
            Log.i("Pet Name", i.petName!!)
        }
        if(petsList.size > 0) {
            rvUserPetListings.visibility = View.VISIBLE
            tvNoListings.visibility = View.GONE

            rvUserPetListings.layoutManager = LinearLayoutManager(activity)
            rvUserPetListings.setHasFixedSize(true)

            val adapterPets = PetsListAdapter(requireActivity(), petsList,this)
            rvUserPetListings.adapter = adapterPets
        } else {
            rvUserPetListings.visibility = View.GONE
            tvNoListings.visibility = View.VISIBLE
        }

    }


    private fun getPetsListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPetsList(this@ListingsFragment)
    }

    override fun onResume() {
        super.onResume()
        getPetsListFromFireStore()
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_pet_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.actionAddPet -> {
                startActivity(Intent(activity,AddPetProfileActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}