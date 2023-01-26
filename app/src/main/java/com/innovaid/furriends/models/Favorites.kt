package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favorites(
    val petIdFavorites: String? = null,
    var userIdFavorites: String? = null,
    val addedToFavorites: Boolean = false

): Parcelable
