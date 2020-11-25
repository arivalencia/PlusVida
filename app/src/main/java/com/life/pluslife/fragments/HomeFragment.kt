package com.life.pluslife.fragments

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.life.pluslife.Constants
import com.life.pluslife.R
import com.life.pluslife.helpers.LocalHelper
import com.life.pluslife.pojos.*
import kotlinx.android.synthetic.main.component_form.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment: Fragment() {

    //private val localHelper = LocalHelper(context!!)
    private val TAG: String = "HomeFragment"
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

                hideForm(user)

            } else {
                db.collection("users").document(user.email!!).get()
                    .addOnSuccessListener {
                        if ( it.exists() ){

                            user.data = Gson().fromJson<UserData>(it["data"].toString(), UserData::class.java)
                            Log.e("USER--", user.toString())
                            context?.let { it1 -> LocalHelper(it1).setUser(user) }

                            hideForm(user)

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

                val diseases = Diseases(
                    if ( rb_respiratory_true.isChecked )      "Si" else "No",
                    if ( rb_gastrointestinal_true.isChecked ) "Si" else "No",
                    if ( rb_nephrourological_true.isChecked ) "Si" else "No",
                    if ( rb_neurological_true.isChecked )     "Si" else "No",
                    if ( rb_hematological_true.isChecked )    "Si" else "No",
                    if ( rb_gynecological_true.isChecked )    "Si" else "No",
                    if ( rb_infectious_true.isChecked )       "Si" else "No",
                    if ( rb_endocrinological_true.isChecked ) "Si" else "No",
                    if ( rb_surgical_true.isChecked )         "Si" else "No",
                    if ( rb_traumatic_true.isChecked )        "Si" else "No",
                    if ( rb_allergic_true.isChecked )         "Si" else "No"
                )

                val contactOne = EmergencyContact(
                    name_contact_one.text.toString(),
                    phone_number_contact_one.text.toString()
                )
                val contactTwo = EmergencyContact(
                    name_contact_two.text.toString(),
                    phone_number_contact_two.text.toString()
                )

                val arrayContacts = ArrayList<EmergencyContact>()
                arrayContacts.add( contactOne )
                arrayContacts.add( contactTwo )

                val userData = UserData(personalInformation, arrayContacts, toxicHabits, diseases)
                val jsonData = Gson().toJson(userData)

                if ( user.email!!.isNotEmpty() ){
                    db.collection("users").document(user.email!!).set(
                        hashMapOf(
                            "data" to jsonData
                        )
                    ).addOnCompleteListener {
                        if ( it.isSuccessful ){
                            user.data = userData
                            context?.let { it1 -> LocalHelper(it1).setUser(user) }

                            Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()

                            hideForm(user)
                        }
                    }
                }
            }
        }

        /*db.collection("users").document(user.email!!).delete()
        user.data = null
        LocalHelper( requireContext() ).setUser(user)*/
    }

    private fun hideForm(user: User){
        title.text = "Hola ${user.data!!.personalInformation.name}"
        registered.visibility = View.VISIBLE
        form.visibility = View.GONE
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

        if ( birthDate == ""){
            Toast.makeText( requireContext(), "Introduzca su fecha de nacimiento", Toast.LENGTH_SHORT ).show()
            boolean = false
        }

        if ( name_contact_one.text.toString().isEmpty() ){
            name_contact_one.error = ""
            boolean = false
        }

        if ( phone_number_contact_one.text.toString().isEmpty() ){
            phone_number_contact_one.error = ""
            boolean = false
        }

        if ( name_contact_two.text.toString().isEmpty() ){
            name_contact_two.error = ""
            boolean = false
        }

        if ( phone_number_contact_two.text.toString().isEmpty() ){
            phone_number_contact_two.error = ""
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

        if ( rg_respiratory.checkedRadioButtonId == -1 ){
            respiratory.error = ""
            boolean = false
        }

        if ( rg_gastrointestinal.checkedRadioButtonId == -1 ){
            gastrointestinal.error = ""
            boolean = false
        }

        if ( rg_nephrourological.checkedRadioButtonId == -1 ){
            nephrourological.error = ""
            boolean = false
        }

        if ( rg_neurological.checkedRadioButtonId == -1 ){
            neurological.error = ""
            boolean = false
        }

        if ( rg_hematological.checkedRadioButtonId == -1 ){
            hematological.error = ""
            boolean = false
        }

        if ( rg_gynecological.checkedRadioButtonId == -1 ){
            gynecological.error = ""
            boolean = false
        }

        if ( rg_infectious.checkedRadioButtonId == -1 ){
            infectious.error = ""
            boolean = false
        }

        if ( rg_endocrinological.checkedRadioButtonId == -1 ){
            endocrinological.error = ""
            boolean = false
        }

        if ( rg_surgical.checkedRadioButtonId == -1 ){
            surgical.error = ""
            boolean = false
        }

        if ( rg_traumatic.checkedRadioButtonId == -1 ){
            traumatic.error = ""
            boolean = false
        }

        if ( rg_allergic.checkedRadioButtonId == -1 ){
            allergic.error = ""
            boolean = false
        }

        return boolean
    }

}