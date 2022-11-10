package com.innovaid.furriends.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
//import androidx.lifecycle.ViewModelProviders
import com.innovaid.furriends.R
import com.innovaid.furriends.viewmodel.FavoritesViewModel
import kotlinx.android.synthetic.main.activity_add_pet_profile.*

class FavoritesFragment : Fragment() {

    //private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       //favoritesViewModel =ViewModelProviders.of(this).get(FavoritesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)
        val textView: TextView = root.findViewById(R.id.tvFavorites)

            textView.text = "This is the Favorite Fragment"

        return root

    }


}