package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.StrayAdoptionForm
import com.innovaid.furriends.ui.activities.admin.ApplicantDetailsActivity
import com.innovaid.furriends.ui.activities.admin.ViewAppointmentDetailsActivity
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.application_list_layout.view.*
import kotlinx.android.synthetic.main.appointment_list_layout.view.*

open class CheckAppointmentListAdapter (
    private val context: Context,
    private val list: ArrayList<StrayAdoptionForm>,


    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder( LayoutInflater.from(parent.context).inflate(
            R.layout.appointment_list_layout,
            parent,
            false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.tvCAApplicantNameValue.text = model.applicantName
        holder.itemView.tvCAApplicantAddressValue.text = model.applicantAddress
        holder.itemView.tvCAApplicantDateValue.text = model.appointmentDate

        //Set Activity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewAppointmentDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_APPLICANT_USER_ID,model.userId)
            intent.putExtra(Constants.EXTRA_STRAY_ID,model.petId)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}