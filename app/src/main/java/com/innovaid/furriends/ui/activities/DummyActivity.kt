package com.innovaid.furriends.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.adapters.PetsListAdapter

class DummyActivity : AppCompatActivity() {

    private lateinit var mRootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var petArrayList: ArrayList<Pet>
    private lateinit var petsListAdapter: PetsListAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)

//        recyclerView = findViewById(R.id.rvUserPetListings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        petArrayList = arrayListOf()
//        petsListAdapter = petsListAdapter(petArrayList)
        recyclerView.adapter = petsListAdapter


        EventChangeListener()
    }
    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("pets")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                    }

                    for(dc: DocumentChange in value?.documentChanges!!) {
                        if(dc.type == DocumentChange.Type.ADDED) {
                            petArrayList.add(dc.document.toObject(Pet::class.java))
                        }
                    }

                    petsListAdapter.notifyDataSetChanged()
                }
            })
    }
}