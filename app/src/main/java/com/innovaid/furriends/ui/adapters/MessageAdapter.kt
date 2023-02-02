package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.firestore.FirebaseFirestore
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.Chat
import com.innovaid.furriends.models.Favorites
import com.innovaid.furriends.ui.activities.MessageActivity
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_pet_details.*
import kotlinx.android.synthetic.main.favorite_stray_layout.view.*
import kotlinx.android.synthetic.main.received_message_layout.view.*
import kotlinx.android.synthetic.main.sent_message_layout.view.*


class MessageAdapter(
    private val context: Context,
    private val list: ArrayList<Chat>,
    private val imageUrl: String
): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            return MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.sent_message_layout,
                    parent,
                    false
                )
            )

        } else {
            return MyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.received_message_layout,
                    parent,
                    false
                )
            )
        }

    }

    override fun getItemViewType(position: Int): Int {
        val model = list[position]
        return if (model.sender == FirestoreClass().getCurrentUserID()) 0 else 1

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (model.sender.toString().equals(FirestoreClass().getCurrentUserID())) {
            if(holder.itemView.tvSentMessage != null) {
            if(holder is MyViewHolder) {
                    holder.itemView.tvSentMessage.text = model.message
                if(holder.itemView.textSentDateTime != null){
                    holder.itemView.textSentDateTime.text = model.timestamp

                }

                }
            }
        } else {
            if(holder.itemView.tvReceivedMessage != null) {
           if (holder is MyViewHolder) {
//               GlideLoader(context).loadUserPicture(imageUrl!!, holder.itemView.ivOtherUserImage)
                   holder.itemView.tvReceivedMessage.text = model.message
               if (holder.itemView.textReceivedDateTime != null) {
                   holder.itemView.textReceivedDateTime.text = model.timestamp

               }

               }
           }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}