package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.activities.user.EditUserPetProfileActivity
import com.innovaid.furriends.ui.activities.user.UserPetDetailsActivity
import com.innovaid.furriends.ui.fragments.user.UserListingsFragment
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.pet_list_layout.view.*
import kotlinx.android.synthetic.main.pet_list_layout.view.ivPetImage

open class PetsListAdapter(
    private val context: Context,
    private val list: ArrayList<Pet>,
    private val fragment: UserListingsFragment
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.pet_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder) {
            GlideLoader(context).loadPetPicture(model.image!!, holder.itemView.ivPetImage)
            holder.itemView.tvListPetName.text = model.petName
            holder.itemView.tvListPetBreed.text = model.petBreed
            holder.itemView.ibDeletePetProfile.setOnClickListener {
                fragment.deletePet(model.petId!!)
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, UserPetDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PET_ID,model.petId)
                intent.putExtra(Constants.EXTRA_PET_OWNER_ID,model.userId)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}




