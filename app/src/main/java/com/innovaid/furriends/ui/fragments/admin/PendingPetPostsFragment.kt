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
import kotlinx.android.synthetic.main.fragment_pending_pet_posts.*


class PendingPetPostsFragment : BaseFragment() {

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
        mRootView = inflater.inflate(R.layout.fragment_pending_pet_posts, container, false)
        return mRootView
    }

   private fun getPendingPetLists() {
       showProgressDialog(resources.getString(R.string.please_wait))
       FirestoreClass().getPendingPetLists(this)
   }
    fun pendingPetPostsLoaded(pendingPetList: ArrayList<Pet>) {
        hideProgressDialog()

        for(i in pendingPetList) {
            Log.i("Pending Pet Name", i.petName!!)
        }

        if(pendingPetList.size > 0) {
            rvPendingPetsList.visibility = View.VISIBLE
            tvNoPendingPetsList.visibility = View.GONE

            rvPendingPetsList.layoutManager = LinearLayoutManager(activity)
            rvPendingPetsList.setHasFixedSize(true)

            val adapterPendingPets = PetPostsAdapter(requireActivity(), pendingPetList, this)
            rvPendingPetsList.adapter = adapterPendingPets
        } else {
            rvPendingPetsList.visibility = View.GONE
            tvNoPendingPetsList.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getPendingPetLists()
    }
}