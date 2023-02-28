package com.innovaid.furriends.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.User
import com.innovaid.furriends.ui.adapters.PetsListAdapter
import com.innovaid.furriends.ui.adapters.UserAdapter
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.fragment_listings.*
import kotlinx.android.synthetic.main.fragment_search_user_chat.*


class SearchUserChatFragment : BaseFragment() {

    private lateinit var mRootView: View
    private lateinit var mUserList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_search_user_chat, container, false)



        return mRootView
    }


    private fun getUserList() {
        FirestoreClass().getUserList(this)
    }
    fun userListSuccessfullyLoadedToHome(userList: ArrayList<User>) {
        for (i in userList) {
            Log.i ("Name", i.firstName!!)
        }
        if (userList.size > 0) {
            rvUserListings.visibility = View.VISIBLE
            tvNoUser.visibility = View.GONE

            rvUserListings.layoutManager = LinearLayoutManager(activity)
            rvUserListings.setHasFixedSize(true)

            val adapterUser = UserAdapter(requireActivity(), userList, true)
            rvUserListings.adapter = adapterUser

            if (etSearchUser != null) {
                etSearchUser.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        val searchText = s.toString().toLowerCase()
                        val query = FirebaseFirestore.getInstance()
                            .collection(Constants.USERS)
                            .orderBy(Constants.FIRST_NAME)
                        query.get().addOnSuccessListener { QuerySnapshot ->
                            Log.d(
                                "SearchResults",
                                "Number of documents found: ${QuerySnapshot.documents.size}"
                            )
                            userList.clear()
                            for (document in QuerySnapshot.documents) {
                                val data = document.toObject(User::class.java)
                                val firstName =
                                    data?.firstName?.toLowerCase() // Convert the value in the database to lowercase for comparison
                                val lastName =
                                    data?.lastName?.toLowerCase() // Convert the value in the database to lowercase for comparison
                                val fullName = "$firstName $lastName"
                                if (data?.id != FirestoreClass().getCurrentUserID() && fullName.containsIgnoreCase(
                                        searchText
                                    )
                                ) { // Use the custom containsIgnoreCase() function to check if the fullName contains the searchText
                                    userList.add(data!!)
                                }

                            }
                            adapterUser.notifyDataSetChanged()
                        }
                    }

                })
            } else {
                rvUserListings.visibility = View.GONE
                tvNoUser.visibility = View.VISIBLE
            }
        }

    }

    fun String.containsIgnoreCase(other: String): Boolean {
        return this.toLowerCase().contains(other.toLowerCase())
    }
    override fun onResume() {
        super.onResume()
        getUserList()
    }


}