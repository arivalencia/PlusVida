package com.life.pluslife.pojos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonalInformation(
    var name: String,
    var motherLastName: String,
    var fatherLastName: String,
    var sex: String,
    var birthDate: String
): Parcelable