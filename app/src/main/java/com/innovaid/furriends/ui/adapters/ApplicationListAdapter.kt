package com.innovaid.furriends.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.models.StrayAdoptionForm
import kotlinx.android.synthetic.main.application_list_layout.view.*

open class ApplicationListAdapter(
    private val list: ArrayList<StrayAdoptionForm>

): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationListAdapter.MyViewHolder {
        return MyViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.application_list_layout,
                parent,
                false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.tvListApplicantName.text = model.applicantName
        holder.itemView.tvListApplicantAddress.text = model.applicantAddress

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}