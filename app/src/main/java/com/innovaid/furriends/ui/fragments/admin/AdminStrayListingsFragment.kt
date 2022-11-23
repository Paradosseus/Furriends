package com.innovaid.furriends.ui.fragments.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.ui.activities.admin.AddStrayAnimalProfileActivity
import com.innovaid.furriends.ui.adapters.PetsListAdapter
import com.innovaid.furriends.ui.adapters.StraysListAdapter
import com.innovaid.furriends.ui.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_admin_stray_listings.*
import kotlinx.android.synthetic.main.fragment_listings.*


class AdminStrayListingsFragment : BaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_stray_listings, container, false)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_pet_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.actionAddPet -> {
                startActivity(Intent(activity, AddStrayAnimalProfileActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun strayAnimalListLoadedSuccessfullyFromFireStore(strayAnimalList: ArrayList<StrayAnimal>) {

        hideProgressDialog()

        for(i in strayAnimalList) {
            Log.i("Stray Animal Breed", i.strayAnimalBreed!!)
        }
        if(strayAnimalList.size > 0) {
            rvStrayAnimalListings.visibility = View.VISIBLE
            tvNoStrayListings.visibility = View.GONE

            rvStrayAnimalListings.layoutManager = LinearLayoutManager(activity)
            rvStrayAnimalListings.setHasFixedSize(true)

            val adapterStrays= StraysListAdapter(requireActivity(), strayAnimalList,this)
            rvStrayAnimalListings.adapter = adapterStrays
        } else {
            rvStrayAnimalListings.visibility = View.GONE
            tvNoStrayListings.visibility = View.VISIBLE
        }

    }
    private fun getStrayAnimalListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getStrayAnimalList(this@AdminStrayListingsFragment)
    }

    override fun onResume() {
        super.onResume()
        getStrayAnimalListFromFireStore()
    }


}