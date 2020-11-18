package com.life.pluslife.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.life.pluslife.R
import com.life.pluslife.helpers.LocalHelper
import com.life.pluslife.pojos.PersonalInformation
import com.life.pluslife.pojos.User
import com.life.pluslife.pojos.UserData
import kotlinx.android.synthetic.main.component_form.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment() {

    val localHelper = getContext()?.let { LocalHelper(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        val user = context?.let { LocalHelper(it).getUser() }

        Log.e("USET", user.toString())


        if (user?.email?.isNotEmpty()!!){
            if ( user.data != null){
                title.text = "Ari Valencia"
                registered.visibility = View.VISIBLE
                form.visibility = View.GONE
                Log.e("1", "1")
            } else {
                db.collection("users").document(user.email!!).get().addOnSuccessListener {
                    if ( it.exists() ){

                        val newObject = Gson().fromJson<UserData>(it["data"].toString(), UserData::class.java)
                        Log.e("NEW OBJECT", newObject.toString())
                        user.data = newObject
                        context?.let { it1 -> LocalHelper(it1).setUser(user) }
                        Log.e("2", "2")
                        title.text = "Ari Valencia"
                        registered.visibility = View.VISIBLE
                        form.visibility = View.GONE
                    } else {
                        title.text = "Ingresar mis datos medicos"
                        registered.visibility = View.GONE
                        form.visibility = View.VISIBLE
                    }
                }
            }
        }

        save.setOnClickListener {
            if ( validateData() ) {
                val personalInformation = PersonalInformation("Ari", "Valencia", "Delgado", "Hombre", "15/08/1999")
                val userData = UserData(personalInformation)
                val jsonData = Gson().toJson(userData)

                user.data = userData
                context?.let { it1 -> LocalHelper(it1).setUser(user) }

                if ( user.email!!.isNotEmpty() ){
                    db.collection("users").document(user.email!!).set(
                        hashMapOf(
                            "data" to jsonData
                        )
                    )
                }
            }
        }
    }

    private fun validateData(): Boolean {
        return false
    }

}