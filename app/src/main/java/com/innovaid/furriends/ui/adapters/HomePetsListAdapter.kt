package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.pet_home_layout.view.*
import kotlinx.android.synthetic.main.pet_list_layout.view.*

open class HomePetsListAdapter(
    private val context: Context,
    private val list: ArrayList<Pet>,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.pet_home_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder) {
            GlideLoader(context).loadPetPicture(model.image!!, holder.itemView.ivHomePetImage)
            holder.itemView.tvHomePetName.text = model.petName
            holder.itemView.tvHomePetBreed.text = model.petBreed
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}