package com.innovaid.furriends.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.lifecycle.ViewModelProviders
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.activities.PetDetailsActivity
import com.innovaid.furriends.ui.adapters.HomePetsListAdapter
import com.innovaid.furriends.ui.adapters.PetsListAdapter
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_listings.*


class HomeFragment : BaseFragment() {
    //private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onResume() {
        super.onResume()
        getPetsListForHome()
    }

    fun petListSuccessfullyLoadedToHome(homePetsList: ArrayList<Pet>) {

        hideProgressDialog()

        if(homePetsList.size > 0) {
            rvHomePetListings.visibility = View.VISIBLE
            tvHomeNoListings.visibility = View.GONE

            rvHomePetListings.layoutManager = GridLayoutManager(activity, 2)
            rvHomePetListings.setHasFixedSize(true)

            val adapterPets = HomePetsListAdapter(requireActivity(), homePetsList)
            rvHomePetListings.adapter = adapterPets

//            adapterPets.setOnClickListener(object: HomePetsListAdapter.OnClickListener {
//                override fun onClick(position: Int, pet: Pet) {
//                    val intent = Intent(context, PetDetailsActivity::class.java)
//                    intent.putExtra(Constants.EXTRA_PET_ID,pet.petId)
//                    startActivity(intent)
//                }
//            })
        } else {
            rvHomePetListings.visibility = View.GONE
            tvHomeNoListings.visibility = View.VISIBLE
        }
    }
    private fun getPetsListForHome() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getPetsListToHome(this@HomeFragment)
    }


}