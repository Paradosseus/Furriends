package com.innovaid.furriends.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserAdoptionForm(
    val applicantName: String? = null,
    val applicantContactNumber: String? = null,
    val applicantAddress: String? = null,
    val userId: String? = null,
    val applicantOccupation: String? = null,
    val questionOneAnswer: String? = null,
    val questionTwoAnswer: String? = null,
    val questionThreeAnswer: String? = null,
    val questionFourAnswer: String? = null,
    val questionFiveAnswer: String? = null,
    val dateTimeIssued: String? = null,
    val reviewStatus: String? = null,
    val petId: String? = null,
    val appointmentDate: String? = null,
    var applicationId: String? = null
): Parcelable

