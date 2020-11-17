package com.life.pluslife.pojos

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
    var personalInformation: PersonalInformation
): Parcelable