package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StrayAdoptionForm(
    val applicantUserId: String? = null,
    val dateIssued: String? = null,
    val timeIssued: String? = null,
    val reviewStatus: String? = null,
    val petId: String? = null
): Parcelable

