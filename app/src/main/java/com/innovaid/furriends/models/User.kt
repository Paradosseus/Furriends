package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val userType: String = "",
    val image: String = "",
    val phoneNumber: Long = 0,
    val address: String = "",
    val bio: String ="",
    val profileCompleted: Int = 0
): Parcelable
