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
import com.innovaid.furriends.models.UserAdoptionForm
import com.innovaid.furriends.ui.adapters.PetApplicationListFragmentAdapter
import com.innovaid.furriends.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_pet_application_status_list.*


class PetApplicationStatusListFragment : BaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_application_status_list, container, false)
    }
    private fun getUserPetApplicationStatusList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserPetApplicationStatus(this)
    }
    fun userPetApplicationStatusListLoadedSuccessfully(petUserApplicationList: ArrayList<UserAdoptionForm>) {
        hideProgressDialog()

        for(i in petUserApplicationList) {
            Log.i("Applicant Name", i.applicantName!!)

            if (petUserApplicationList.size > 0) {
                rvPetApplicationStatusList.visibility = View.VISIBLE
                tvNoPetApplicationStatusList.visibility = View.GONE

                rvPetApplicationStatusList.layoutManager = LinearLayoutManager(activity)
                rvPetApplicationStatusList.setHasFixedSize(true)

                val userPetApplicationAdapter = PetApplicationListFragmentAdapter(requireActivity(), petUserApplicationList)
                rvPetApplicationStatusList.adapter = userPetApplicationAdapter
            } else {
                rvPetApplicationStatusList.visibility = View.GONE
                tvNoPetApplicationStatusList.visibility = View.VISIBLE
            }
        }
    }
    override fun onStart() {
        super.onStart()
        getUserPetApplicationStatusList()
    }

}