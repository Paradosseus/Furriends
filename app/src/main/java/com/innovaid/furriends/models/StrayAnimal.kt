package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StrayAnimal(
    val userId: String? = null,
    val strayAnimalBreed: String? = null,
    val strayAnimalColor: String? =null,
    val strayAnimalType: String? = null,
    val strayAnimalGender: String?  =null,
    val strayAnimalSpayedOrNeutered: String? =null,
    val locationFounded: String? = null,
    val dateFounded: String? = null,
    val timeFounded: String? = null,
    val strayAnimalDescription: String? = null,
    val strayAnimalImage: String? = null,
    val status: String? = null,
    val adoptionStatus: String? =null,
    val adoptedTo: String? = null,
    var strayAnimalId: String? = null
) : Parcelable
