package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.fragments.ListingsFragment
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_pet_profile.view.*
import kotlinx.android.synthetic.main.pet_list_layout.view.*
import kotlinx.android.synthetic.main.pet_list_layout.view.ivPetImage

class PetsListAdapter(private val petsList: ArrayList<Pet>): RecyclerView.Adapter<PetsListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsListAdapter.MyViewHolder {

            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.pet_list_layout,
                parent,
                false

            )
        return MyViewHolder(itemView)

    }



    override fun getItemCount(): Int {
        return petsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pet: Pet  = petsList[position]

        if(holder is MyViewHolder) {
            //GlideLoader(context).loadPetPicture(pet.image, holder.itemView.ivPetImage)
            holder.itemView.tvListPetName.text = pet.petName
            holder.itemView.tvListPetBreed.text = pet.petBreed

        }
    }
}
