package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.firestore.FirestoreClass
import com.innovaid.furriends.models.RecentChats
import com.innovaid.furriends.ui.activities.MessageActivity
import com.innovaid.furriends.utils.Constants
import com.innovaid.furriends.utils.GlideLoader
import kotlinx.android.synthetic.main.chat_list_item_layout.view.*
import kotlinx.android.synthetic.main.manage_pet_post_list_layout.view.*


class ChatsListAdapter(
    private val context: Context,
    private val list: ArrayList<RecentChats>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.chat_list_item_layout, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){
            if (model.userId2 == FirestoreClass().getCurrentUserID()) {
                GlideLoader(context).loadUserPicture(model.userImage!!, holder.itemView.ivOtherUserImage)
                holder.itemView.tvChatListUsername.text = model.firstName
                holder.itemView.tvMessagePreview.text = model.lastMessage
                holder.itemView.tvDateTimeMessage.text = model.timestamp

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, MessageActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_ID, model.userId)
                    context.startActivity(intent)
                }
            } else {
                holder.itemView.tvChatListUsername.text = model.userId
            }



        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}
