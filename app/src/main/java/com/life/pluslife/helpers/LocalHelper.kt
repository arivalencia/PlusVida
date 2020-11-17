package com.life.pluslife.helpers

import android.content.Context
import com.google.gson.Gson
import com.life.pluslife.R
import com.life.pluslife.pojos.User

class LocalHelper(val context: Context) {

    fun setUser(user: User?) {
        val preferences = context.getSharedPreferences(
            context.getString(R.string.preferences_user), Context.MODE_PRIVATE )
        val editor = preferences.edit()

        if (user != null) {
            val json = Gson().toJson(user)
            editor.putString("user", json)
        } else {
            editor.putString("user", null)
        }
        editor.apply()
    }

    fun getUser(): User? {
        val preferences = context.getSharedPreferences(
            context.getString(R.string.preferences_user), Context.MODE_PRIVATE )
        val serializedData = preferences.getString("user", null)
        val gson = Gson()
        return gson.fromJson<User>(serializedData, User::class.java)
    }

}