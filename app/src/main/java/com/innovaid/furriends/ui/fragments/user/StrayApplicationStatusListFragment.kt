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
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.ui.adapters.StrayApplicationListFragmentAdapter
import com.innovaid.furriends.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_stray_application_status_list.*

class StrayApplicationStatusListFragment : BaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stray_application_status_list, container, false)
    }
    private fun getUserStrayApplicationStatusList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserStrayApplicationStatusList(this)
    }
    fun userStrayApplicationStatusListLoadedSuccessfully(strayUserApplicationList: ArrayList<StrayAdoptionForm>) {
        hideProgressDialog()

        for (i in strayUserApplicationList) {
            Log.i("Applicant Name", i.applicantName!!)

            if(strayUserApplicationList.size > 0) {
                rvStrayApplicationStatusList.visibility = View.VISIBLE
                tvNoStrayApplicationStatusList.visibility = View.GONE

                rvStrayApplicationStatusList.layoutManager = LinearLayoutManager(activity)
                rvStrayApplicationStatusList.setHasFixedSize(true)

                val adapterUserStrayApplicationAdapter = StrayApplicationListFragmentAdapter(requireActivity(), strayUserApplicationList)
                rvStrayApplicationStatusList.adapter = adapterUserStrayApplicationAdapter

            }else {
                rvStrayApplicationStatusList.visibility = View.GONE
                tvNoStrayApplicationStatusList.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getUserStrayApplicationStatusList()
    }

}