package com.innovaid.furriends.ui.fragments.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.ui.adapters.AdminHomeAdapter
import com.innovaid.furriends.ui.adapters.StraysListAdapter
import com.innovaid.furriends.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_admin_home.*
import kotlinx.android.synthetic.main.fragment_admin_stray_listings.*


class AdminHomeFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false)
    }
    override fun onResume() {
        super.onResume()
        getStrayAnimalListFromFireStore()
    }
    private fun getStrayAnimalListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getStrayAnimalList(this@AdminHomeFragment)
    }
    fun strayAnimalListLoadedSuccessfullyFromFireStore(strayAnimalList: ArrayList<StrayAnimal>) {

        hideProgressDialog()

        for(i in strayAnimalList) {
            Log.i("Stray Animal Breed", i.strayAnimalBreed!!)
        }
        if(strayAnimalList.size > 0) {
            rvHomeStrayAnimalListings.visibility = View.VISIBLE
            tvHomeNoStrayListings.visibility = View.GONE

            rvHomeStrayAnimalListings.layoutManager = GridLayoutManager(activity, 2)
            rvHomeStrayAnimalListings.setHasFixedSize(true)

            val adapterStrays= AdminHomeAdapter(requireActivity(), strayAnimalList)
            rvHomeStrayAnimalListings.adapter = adapterStrays
        } else {
            rvHomeStrayAnimalListings.visibility = View.GONE
            tvHomeNoStrayListings.visibility = View.VISIBLE
        }

    }

}