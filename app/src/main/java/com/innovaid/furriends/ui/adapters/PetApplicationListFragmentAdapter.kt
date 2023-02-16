package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.UserAdoptionForm
import com.innovaid.furriends.ui.activities.user.PetUserApplicationStatusActivity
import com.innovaid.furriends.ui.activities.user.UserApplicationStatusActivity
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.pet_user_application_item_layout.view.*

class PetApplicationListFragmentAdapter(
    private val context: Context,
    private val list: ArrayList<UserAdoptionForm>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.pet_user_application_item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder) {
            holder.itemView.tvPUALApplicationIdValue.text = model.applicationId
            holder.itemView.tvPUALDateTimeIssued.text = model.dateTimeIssued
            holder.itemView.tvPUALApplicationStatusValue.text = model.reviewStatus

            holder.itemView.setOnClickListener {
                val intent = Intent(context, PetUserApplicationStatusActivity::class.java)
                intent.putExtra(Constants.EXTRA_APPLICATION_ID, model.applicationId)
                intent.putExtra(Constants.EXTRA_PET_ID, model.petId)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)


}