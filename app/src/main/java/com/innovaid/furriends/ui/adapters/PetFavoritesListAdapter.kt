package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Favorites
import com.innovaid.furriends.ui.activities.user.UserPetDetailsActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.favorite_pet_layout.view.*

class PetFavoritesListAdapter (
    private val context: Context,
    private val list: ArrayList<Favorites>,

    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.favorite_pet_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadPetPicture(model.image!!, holder.itemView.ivPetFavoriteImage)
            holder.itemView.tvPetFavoriteName.text = model.petName
            holder.itemView.tvPetFavoriteLocation.text = model.location

            holder.itemView.setOnClickListener {
                val intent = Intent(context, UserPetDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PET_ID,model.petId)
                intent.putExtra(Constants.EXTRA_PET_OWNER_ID,model.ownerId)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size

    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}