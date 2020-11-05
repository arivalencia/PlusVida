package com.life.pluslife.pojos

import android.os.Parcel
import android.os.Parcelable

data class User(
    var email: String? = null,
    var name: String? = null,
    var lastName: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this() {
        email = parcel.readString()
        name = parcel.readString()
        lastName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(lastName)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "(email=$email, name=$name, lastName=$lastName)"
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}