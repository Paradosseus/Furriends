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
import kotlinx.android.synthetic.main.fragment_approved_pet_posts.*


class ApprovedPetPostsFragment : BaseFragment() {

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
        mRootView = inflater.inflate(R.layout.fragment_approved_pet_posts, container, false)
        return mRootView
    }

    private fun getApprovedPetLists() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getApprovedPetLists(this)
    }
    fun approvedPetPostsLoaded(approvedPetList: ArrayList<Pet>) {
        hideProgressDialog()

        for(i in approvedPetList) {
            Log.i("Pending Pet Name", i.petName!!)
        }

        if(approvedPetList.size > 0) {
            rvApprovedPetsList.visibility = View.VISIBLE
            tvNoApprovedPetsList.visibility = View.GONE

            rvApprovedPetsList.layoutManager = LinearLayoutManager(activity)
            rvApprovedPetsList.setHasFixedSize(true)

            val adapterPendingPets = PetPostsAdapter(requireActivity(), approvedPetList, this)
            rvApprovedPetsList.adapter = adapterPendingPets
        } else {
            rvApprovedPetsList.visibility = View.GONE
            tvNoApprovedPetsList.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getApprovedPetLists()
    }

}