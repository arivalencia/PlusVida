package com.life.pluslife.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.life.pluslife.R
import com.life.pluslife.helpers.LocalHelper
import com.life.pluslife.pojos.PersonalInformation
import com.life.pluslife.pojos.ToxicHabits
import com.life.pluslife.pojos.UserData
import kotlinx.android.synthetic.main.component_form.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class HomeFragment: Fragment() {

    //private val localHelper = LocalHelper(context!!)
    private val db = FirebaseFirestore.getInstance()
    private var birthDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = context?.let { LocalHelper(it).getUser() }
        Log.e("USER", user.toString())


        if (user?.email?.isNotEmpty()!!){
            if ( user.data != null){

                title.text = "Datos de ${user.data!!.personalInformation.name}"
                registered.visibility = View.VISIBLE
                form.visibility = View.VISIBLE

            } else {
                db.collection("users").document(user.email!!).get().addOnSuccessListener {
                    if ( it.exists() ){

                        user.data = Gson().fromJson<UserData>(it["data"].toString(), UserData::class.java)
                        context?.let { it1 -> LocalHelper(it1).setUser(user) }

                        title.text = "Datos de ${user.data!!.personalInformation.name}"
                        registered.visibility = View.VISIBLE
                        form.visibility = View.GONE

                    } else {
                        title.text = "Ingresar mis datos de ayuda"
                        registered.visibility = View.GONE
                        form.visibility = View.VISIBLE
                    }
                }
            }
        }

        calendar.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val dialogDate = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    birthDate = "$i3/$i2/$i"
                    birth_date.text = "$i3/${i2+1}/$i"
                },
                year,
                month,
                day
            ).show()
        }

        save.setOnClickListener {
            if ( validateData() ) {
                val personalInformation = PersonalInformation(
                    name.text.toString(),
                    mother_last_name.text.toString(),
                    father_last_name.text.toString(),
                    spinner_sex.selectedItem.toString(),
                    birthDate,
                    weight.text.toString().toInt(),
                    height.text.toString().toDouble(),
                    spinner_blood_types.selectedItem.toString()
                )

                val toxicHabits = ToxicHabits(
                    spinner_alcohol.selectedItem.toString(),
                    spinner_tobacco.selectedItem.toString(),
                    spinner_drugs.selectedItem.toString()
                )

                Log.e("PERSONAL INFORMATION", personalInformation.toString())
                Log.e("TOXIC HABITS", toxicHabits.toString())
                /*val userData = UserData(personalInformation)
                val jsonData = Gson().toJson(userData)

                user.data = userData
                context?.let { it1 -> LocalHelper(it1).setUser(user) }

                if ( user.email!!.isNotEmpty() ){
                    db.collection("users").document(user.email!!).set(
                        hashMapOf(
                            "data" to jsonData
                        )
                    )
                }*/
            }
        }
    }

    private fun validateData(): Boolean {
        var boolean = true

        if ( name.text.toString().isEmpty() ){
            name.error = "Se necesita un nombre"
            boolean = false
        }

        if ( mother_last_name.text.toString().isEmpty() ){
            mother_last_name.error = "Se necesita un apellido"
            boolean = false
        }

        if ( father_last_name.text.toString().isEmpty() ){
            father_last_name.error = "Se necesita un apellido"
            boolean = false
        }

        if ( weight.text.toString().isEmpty() ){
            weight.error = "Ingrese su peso"
            boolean = false
        }

        if ( height.text.toString().isEmpty() ){
            height.error = "Ingrese su altura"
            boolean = false
        }

        if ( spinner_sex.selectedItemPosition == 0 ){
            Toast.makeText( requireContext(), "Seleccione un sexo", Toast.LENGTH_SHORT ).show()
            boolean = false
        }

        if ( spinner_blood_types.selectedItemPosition == 0 ) {
            Toast.makeText( requireContext(), "Seleccione su tipo de sangre", Toast.LENGTH_SHORT ).show()
            boolean = false
        }

        if ( spinner_alcohol.selectedItemPosition == 0 ) {
            Toast.makeText( requireContext(), "Selecciona tu consumo de alcohol", Toast.LENGTH_SHORT).show()
            boolean = false
        }

        if ( spinner_tobacco.selectedItemPosition == 0 ) {
            Toast.makeText( requireContext(), "Selecciona tu consumo de tabaco", Toast.LENGTH_SHORT).show()
            boolean = false
        }

        if ( spinner_drugs.selectedItemPosition == 0 ) {
            Toast.makeText( requireContext(), "Selecciona tu consumo de drogas", Toast.LENGTH_SHORT).show()
            boolean = false
        }

        if ( birthDate == ""){
            Toast.makeText( requireContext(), "Introduzca su fecha de nacimiento", Toast.LENGTH_SHORT ).show()
            boolean = false
        }



        return boolean
    }

}