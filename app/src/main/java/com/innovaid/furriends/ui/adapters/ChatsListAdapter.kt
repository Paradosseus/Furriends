package com.innovaid.furriends.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innovaid.furriends.R
import com.innovaid.furriends.models.Chat
import com.innovaid.furriends.models.Pet
import com.innovaid.furriends.ui.fragments.ChatsListFragment
import com.innovaid.furriends.ui.fragments.user.UserListingsFragment

class ChatsListAdapter(
    private val context: Context,
    private val list: ArrayList<Chat>,
    private val fragment: ChatsListFragment
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.chat_list_item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder) {
//            holder.itemView.
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}