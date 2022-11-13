package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pet(
    val userId: String = "",
    val userName: String,
    val petName: String = "",
    val petBreed: String = "",
    val petBirthDate: String = "",
    val petColor: String ="",
    val petType: String = "",
    val petGender: String  ="",
    val spayedOrNeutered: String ="",
    val description: String = "",
    val image: String = "",
    var petId: String = ""
): Parcelable
