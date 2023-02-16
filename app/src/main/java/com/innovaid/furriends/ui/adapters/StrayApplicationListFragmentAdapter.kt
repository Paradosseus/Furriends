package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.ui.activities.user.UserApplicationStatusActivity
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.stray_user_application_item_layout.view.*

class StrayApplicationListFragmentAdapter(
    private val context: Context,
    private val list: ArrayList<StrayAdoptionForm>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.stray_user_application_item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder) {
            holder.itemView.tvSUALApplicationIdValue.text = model.applicationId
            holder.itemView.tvSUALDateTimeValue.text = "${model.dateIssued} at ${model.timeIssued}"
            holder.itemView.tvSUALApplicationStatusValue.text = model.reviewStatus

            holder.itemView.setOnClickListener {
                val intent = Intent(context, UserApplicationStatusActivity::class.java)
                intent.putExtra(Constants.EXTRA_APPLICATION_ID, model.applicationId)
                intent.putExtra(Constants.EXTRA_STRAY_ID, model.petId)
                context.startActivity(intent)
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}