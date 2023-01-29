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
import com.innovaid.furriends.ui.adapters.PetsListAdapter
import com.innovaid.furriends.ui.adapters.StrayFavoritesListAdapter
import com.innovaid.furriends.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_favorites_stray.*
import kotlinx.android.synthetic.main.fragment_listings.*


class FavoritesStrayFragment : BaseFragment() {

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
        mRootView = inflater.inflate(R.layout.fragment_favorites_stray, container, false)
        return mRootView
    }

    private fun getStrayFavoritesList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getStrayFavoritesList(this)
    }
    fun strayFavoritesLoadedSuccessfully(strayFavoritesList: ArrayList<Favorites>) {

        hideProgressDialog()

        for(i in strayFavoritesList) {
            Log.i("Breed", i.breed!!)
        }
        if(strayFavoritesList.size > 0) {
            rvStrayFavoritesList.visibility = View.VISIBLE
            tvNoStrayFavoritesList.visibility = View.GONE

            rvStrayFavoritesList.layoutManager = LinearLayoutManager(activity)
            rvStrayFavoritesList.setHasFixedSize(true)

            val adapterStrayFavorites = StrayFavoritesListAdapter(requireActivity(), strayFavoritesList)
            rvStrayFavoritesList.adapter = adapterStrayFavorites
        } else {
            rvStrayFavoritesList.visibility = View.GONE
            tvNoStrayFavoritesList.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        getStrayFavoritesList()
    }
}