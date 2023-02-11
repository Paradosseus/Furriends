package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chat(
    val senderId: String? = null,
    val receiverId: String? = null,
    val message: String? = null,
    val imageUrl: String? = null,
    var messageId: String? = null,
    val timestamp: String? = null,
): Parcelable