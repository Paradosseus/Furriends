package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RecentChats(
    val userId: String? = null,
    val userId2: String? = null,
    val firstName : String? = null,
    val lastName : String? = null,
    val userImage: String? = null,
    val lastMessage: String? =null,
    val timestamp: String? = null,
    var messageId:String? = null,

): Parcelable