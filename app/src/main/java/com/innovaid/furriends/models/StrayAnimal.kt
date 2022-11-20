package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StrayAnimal(
    val breed: String? = null,
    val petColor: String? =null,
    val petType: String? = null,
    val petGender: String?  =null,
    val spayedOrNeutered: String? =null,
    val location: String? = null,
    val dateFounded: String? = null,
    val timeFounded: String? = null,
    val description: String? = null,
    val image: String? = null,
    val status: String? = null,
    val adoptedTo : String? = null,
    val strayAnimalId: String? = null
) : Parcelable
