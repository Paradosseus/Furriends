package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.UserAdoptionForm
import com.innovaid.furriends.ui.activities.admin.ApplicantDetailsActivity
import com.innovaid.furriends.ui.activities.user.UserPetApplicationDetailsActivity
import com.innovaid.furriends.utils.Constants
import kotlinx.android.synthetic.main.user_pet_applicant_item_layout.view.*

class reviewUserPetApplicationAdapter(
    private val context: Context,
    private val list: ArrayList<UserAdoptionForm>

    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_pet_applicant_item_layout,
            parent
        ,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.tvUPApplicantNameValue.text = model.applicantName
        holder.itemView.tvUPApplicantAddressValue.text = model.applicantAddress
        holder.itemView.tvUPApplicantApplicationStatusValue.text = model.reviewStatus

        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserPetApplicationDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_APPLICANT_USER_ID,model.applicationId)
            intent.putExtra(Constants.EXTRA_PET_ID,model.petId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}