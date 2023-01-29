package com.innovaid.furriends.models

import android.media.MediaRouter.RouteCategory
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favorites(
    val petId: String? = null,
    val userId: String? = null,
    val category: String? = null,
    val ownerId: String? = null,
    val breed: String? = null,
    val location: String? = null,
    val image: String? = null,
    val petName: String? = null,
    var favoritesId: String? = null

): Parcelable
