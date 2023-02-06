package com.innovaid.furriends.ui.fragments.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
//import androidx.lifecycle.ViewModelProviders
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.activities.user.UserPetDetailsActivity
import com.innovaid.furriends.ui.adapters.HomePetsListAdapter
import com.innovaid.furriends.ui.fragments.BaseFragment
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.fragment_user_home.*


class UserHomeFragment : BaseFragment() {
    //private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_user_home, container, false)

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

            adapterPets.setOnClickListener(object: HomePetsListAdapter.OnClickListener {
                override fun onClick(position: Int, pet: Pet) {
                    val intent = Intent(context, UserPetDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PET_ID,pet.petId)
                    startActivity(intent)
                }
            })
        } else {
            rvHomePetListings.visibility = View.GONE
            tvHomeNoListings.visibility = View.VISIBLE
        }
    }
    private fun getPetsListForHome() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getPetsListToHome(this@UserHomeFragment)
    }


}