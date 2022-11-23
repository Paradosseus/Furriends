package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.StrayAnimal
import com.innovaid.furriends.ui.activities.admin.StrayAnimalDetailsActivity
import com.innovaid.furriends.ui.activities.user.UserPetDetailsActivity
import com.innovaid.furriends.ui.fragments.admin.AdminHomeFragment
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.pet_home_layout.view.*
import kotlinx.android.synthetic.main.pet_list_layout.view.*

open class AdminHomeAdapter(
    private val context: Context,
    private val list: ArrayList<StrayAnimal>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.pet_home_layout,
                parent,
                false
            )
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder) {
            GlideLoader(context).loadPetPicture(model.strayAnimalImage!!, holder.itemView.ivHomePetImage)
            holder.itemView.tvHomePetName.text = model.strayAnimalBreed
            holder.itemView.tvHomePetBreed.text = model.locationFounded

            holder.itemView.setOnClickListener {
                val intent = Intent(context, StrayAnimalDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_STRAY_ANIMAL_ID,model.strayAnimalId)
                intent.putExtra(Constants.EXTRA_STRAY_OWNER_ID,model.userId)
                context.startActivity(intent)
//            holder.itemView.setOnClickListener {
//                if(onClickListener != null) {
//                    onClickListener!!.onClick(position, model)
//                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick(position: Int, strayAnimal: StrayAnimal)
    }
}