package com.innovaid.furriends.ui.fragments.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.adapters.PetPostsAdapter
import com.innovaid.furriends.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_declined_pet_posts.*

class DeclinedPetPostsFragment : BaseFragment() {

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
        mRootView = inflater.inflate(R.layout.fragment_declined_pet_posts, container, false)
        return mRootView
    }

    private fun getDeclinedPetLists() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDeclinedPetLists(this)
    }
    fun declinedPetPostsLoaded(declinedPetList: ArrayList<Pet>) {
        hideProgressDialog()

        for(i in declinedPetList) {
            Log.i("Declined Pet Name", i.petName!!)
        }

        if(declinedPetList.size > 0) {
            rvDeclinedPetsList.visibility = View.VISIBLE
            tvNoDeclinedPetsList.visibility = View.GONE

            rvDeclinedPetsList.layoutManager = LinearLayoutManager(activity)
            rvDeclinedPetsList.setHasFixedSize(true)

            val adapterPendingPets = PetPostsAdapter(requireActivity(), declinedPetList, this)
            rvDeclinedPetsList.adapter = adapterPendingPets
        } else {
            tvNoDeclinedPetsList.visibility = View.GONE
            tvNoDeclinedPetsList.visibility = View.VISIBLE
        }
    }
    override fun onResume() {
        super.onResume()
        getDeclinedPetLists()
    }

}