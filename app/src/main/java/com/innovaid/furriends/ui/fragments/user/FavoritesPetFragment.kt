package com.innovaid.furriends.ui.fragments.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.ui.fragments.BaseFragment


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
    private fun getStrayFavoritesList() {
        showProgressDialog(resources.getString(R.string.please_wait))
//        FirestoreClass().getStrayFavoritesList(this)
    }


}