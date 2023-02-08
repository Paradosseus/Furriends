package com.innovaid.furriends.ui.fragments.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Favorites
import com.innovaid.furriends.ui.adapters.PetFavoritesListAdapter
import com.innovaid.furriends.ui.adapters.StrayFavoritesListAdapter
import com.innovaid.furriends.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_favorites_pet.*
import kotlinx.android.synthetic.main.fragment_favorites_stray.*


class FavoritesPetFragment : BaseFragment() {

    private lateinit var mRootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_favorites_pet, container, false)
        return mRootView
    }
    private fun getPetFavoritesList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPetFavoritesList(this)
    }
    fun petFavoritesLoadedSuccessfully(petFavoritesList: ArrayList<Favorites>) {
        hideProgressDialog()

        for(i in petFavoritesList) {
            Log.i("Name", i.petName!!)
        }
        if(petFavoritesList.size > 0) {
            rvPetFavoriteList.visibility = View.VISIBLE
            tvNoPetFavoritesList.visibility = View.GONE

            rvPetFavoriteList.layoutManager = LinearLayoutManager(activity)
            rvPetFavoriteList.setHasFixedSize(true)

            val adapterPetFavorites = PetFavoritesListAdapter(requireActivity(), petFavoritesList)
            rvPetFavoriteList.adapter = adapterPetFavorites
        } else {
            rvPetFavoriteList.visibility = View.GONE
            tvNoPetFavoritesList.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        getPetFavoritesList()
    }

}