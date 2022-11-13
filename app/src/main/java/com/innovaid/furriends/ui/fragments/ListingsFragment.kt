package com.innovaid.furriends.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.activities.AddPetProfileActivity
import com.innovaid.furriends.ui.adapters.PetsListAdapter
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_listings.*

class ListingsFragment : BaseFragment() {

    private lateinit var mRootView: View

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

            val adapterPets = PetsListAdapter(requireActivity(), petsList)
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