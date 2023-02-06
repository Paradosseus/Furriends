package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pet(
    val userId: String? = null,
    val userName: String? = null,
    val petName: String? = null,
    val petBreed: String? = null,
    val petLocation: String? = null,
    val petBirthDate: String? = null,
    val petColor: String? =null,
    val petType: String? = null,
    val petGender: String?  =null,
    val spayedOrNeutered: String? =null,
    val description: String? = null,
    val image: String? = null,
    val approvalStatus: String? = null,
    val adoptionStatus: String? =null,
    val category: String? = null,
    val adoptedTo: String? = null,
    var petId: String? = null

): Parcelable
