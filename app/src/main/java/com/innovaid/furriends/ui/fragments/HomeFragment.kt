package com.innovaid.furriends.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.viewmodel.HomeViewModel


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

//    override fun onResume() {
//        super.onResume()
//        getpetsListForHome()
//    }

//    fun petListSuccessfullyLoadedToHome(homePetsList: ArrayList<Pet>) {
//        hideProgressDialog()
//        for (i in homePetsList) {
//            Log.i("Pets name", i.petName)
//        }
//    }
//    private fun getpetsListForHome() {
//        showProgressDialog(resources.getString(R.string.please_wait))
//
//        FirestoreClass().getPetsListToHome(this@HomeFragment)
//    }


}