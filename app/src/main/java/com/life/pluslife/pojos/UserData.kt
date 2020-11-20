package com.life.pluslife.pojos

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
    var personalInformation: PersonalInformation,
    var toxicHabits: ToxicHabits,
    var diseases: Diseases
): Parcelable

@Parcelize
data class PersonalInformation(
    var name: String,
    var motherLastName: String,
    var fatherLastName: String,
    var sex: String,
    var birthDate: String,
    var weight: Int,
    var height: Double,
    var bloodType: String
): Parcelable {
    fun getFullName(): String {
        return "$name $motherLastName $fatherLastName"
    }
}

@Parcelize
data class ToxicHabits(
    var alcohol: String,
    var tobacco: String,
    var drugs: String
): Parcelable

@Parcelize
data class Diseases(
    var respiratory: String,
    var gastrointestinal: String,
    var nephrourological: String,
    var neurological: String,
    var hematological: String,
    var gynecological: String,
    var infectious: String,
    var endocrinological: String,
    var surgical: String,
    var traumatic: String,
    var allergic: String
): Parcelable