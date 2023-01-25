package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StrayAdoptionForm(
    val applicantName: String? = null,
    val applicantAddress: String? = null,
    val userId: String? = null,
    val dateIssued: String? = null,
    val timeIssued: String? = null,
    val reviewStatus: String? = null,
    val strayAnimalAdoptionForm: String? = null,
    val petId: String? = null,
    val appointmentDate: String? = null,
    var applicationId: String? = null
): Parcelable

