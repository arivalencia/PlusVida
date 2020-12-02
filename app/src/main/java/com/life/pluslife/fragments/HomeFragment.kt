package com.life.pluslife.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
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
import com.life.pluslife.pojos.*
import kotlinx.android.synthetic.main.component_form.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment: Fragment() {

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
        val calendar = Calendar.getInstance()

        if (user?.email?.isNotEmpty()!!){
            if ( user.data != null){
                Log.e("INSIDE BY ", "user.data")
                hideForm(user, view)
            } else {
                db.collection("users").document(user.email!!).get()
                    .addOnSuccessListener {
                        if ( it.exists() ){

                            Log.e("INSIDE BY ", "firebase SUCCESS")
                            user.data = Gson().fromJson<UserData>(it["data"].toString(), UserData::class.java)
                            Log.e("USER FROM FIREBASE", user.toString())
                            context?.let { it1 -> LocalHelper(it1).setUser(user) }

                            hideForm(user, view)

                        } else {
                            Log.e("INSIDE BY ", "no data")
                            showForm(view)
                        }
                    }
            }
        }

        birth_date.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    birthDate = "$i3/$i2/$i"
                    birth_date.text = "$i3/${i2+1}/$i"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
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

                var allergies: String = ""
                if ( rb_allergies_true.isChecked ) {
                    allergies = input_allergies.text.toString()
                }

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
                    if ( rb_traumatic_true.isChecked )        "Si" else "No"
                )

                val arrayContacts = ArrayList<EmergencyContact>()
                arrayContacts.add(
                    EmergencyContact(
                        name_contact_one.text.toString(),
                        phone_number_contact_one.text.toString()
                    )
                )
                arrayContacts.add(
                    EmergencyContact(
                        name_contact_two.text.toString(),
                        phone_number_contact_two.text.toString()
                    )
                )

                val arrayMedicines = ArrayList<Medicine>()
                if ( rb_medicines_true.isChecked ) {
                    arrayMedicines.add(
                        Medicine(
                            medicine_0.text.toString(),
                            units_0.text.toString(),
                            lapse_0.text.toString(),
                            time_0.text.toString()
                        )
                    )
                    arrayMedicines.add(
                        Medicine(
                            medicine_1.text.toString(),
                            units_1.text.toString(),
                            lapse_1.text.toString(),
                            time_1.text.toString()
                        )
                    )
                }

                val userData = UserData(personalInformation, arrayContacts, allergies, toxicHabits, diseases, arrayMedicines)
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
                            hideForm(user, view)
                        }
                    }
                }
            }
        }

        btn_edit_data.setOnClickListener {
            showForm(view)
            if ( user.data != null ) {
                fillData(user.data!!)
            }
        }

        rb_allergies_false.setOnClickListener {
            container_input_allergies.visibility = View.GONE
        }

        rb_allergies_true.setOnClickListener {
            container_input_allergies.visibility = View.VISIBLE
        }

        rb_medicines_false.setOnClickListener {
            container_medicines.visibility = View.GONE
        }

        rb_medicines_true.setOnClickListener {
            container_medicines.visibility = View.VISIBLE
        }

        time_0.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                OnTimeSetListener { view, hourOfDay, minute ->
                    time_0.setText( "${hourOfDay}:${minute}" )
                },
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }

        time_1.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                OnTimeSetListener { view, hourOfDay, minute ->
                    time_1.setText( "${hourOfDay}:${minute}" )
                },
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }

        /*db.collection("users").document(user.email!!).delete()
        user.data = null
        LocalHelper( requireContext() ).setUser(user)*/
    }

    private fun hideForm(user: User, view: View){
        view.title_page.text = "Hola " + (user.data?.personalInformation?.name ?: "")
        view.registered.visibility = View.VISIBLE
        view.form.visibility = View.GONE

        val medicines = user.data!!.medicines

        if ( medicines.isEmpty() ) {
            view.no_medicines.visibility = View.VISIBLE
            view.container_all_medicines.visibility = View.GONE
        } else {
            view.no_medicines.visibility = View.GONE
            view.container_all_medicines.visibility = View.VISIBLE
            view.txt_medicine_0.text = medicines[0].name
            view.txt_units_0.text = medicines[0].units
            view.txt_time_0.text = medicines[0].time
            view.txt_lapse_0.text = medicines[0].lapse

            if ( medicines[1].name.isEmpty() ) {
                view.container_medicine_1.visibility = View.GONE
            } else {
                view.container_medicine_1.visibility = View.VISIBLE

                view.txt_medicine_1.text = medicines[1].name
                view.txt_units_1.text = medicines[1].units
                view.txt_time_1.text = medicines[1].time
                view.txt_lapse_1.text = medicines[1].lapse
            }
        }
    }

    private fun showForm(view: View) {
        view.title_page.text = "Ingresar mis datos de ayuda"
        view.registered.visibility = View.GONE
        view.form.visibility = View.VISIBLE
    }

    private fun validateData(): Boolean {
        var success = true

        if ( name.text.toString().isEmpty() ){
            name.error = "Se necesita un nombre"
            success = false
        }

        if ( mother_last_name.text.toString().isEmpty() ){
            mother_last_name.error = "Se necesita un apellido"
            success = false
        }

        if ( father_last_name.text.toString().isEmpty() ){
            father_last_name.error = "Se necesita un apellido"
            success = false
        }

        if ( weight.text.toString().isEmpty() ){
            weight.error = "Ingrese su peso"
            success = false
        }

        if ( height.text.toString().isEmpty() ){
            height.error = "Ingrese su altura"
            success = false
        }

        if ( spinner_sex.selectedItemPosition == 0 ){
            Toast.makeText( requireContext(), "Seleccione un sexo", Toast.LENGTH_SHORT ).show()
            success = false
        }

        if ( spinner_blood_types.selectedItemPosition == 0 ) {
            Toast.makeText( requireContext(), "Seleccione su tipo de sangre", Toast.LENGTH_SHORT ).show()
            success = false
        }

        if ( birthDate == ""){
            Toast.makeText( requireContext(), "Introduzca su fecha de nacimiento", Toast.LENGTH_SHORT ).show()
            success = false
        }

        if ( name_contact_one.text.toString().isEmpty() ){
            name_contact_one.error = ""
            success = false
        }

        if ( phone_number_contact_one.text.toString().isEmpty() ){
            phone_number_contact_one.error = ""
            success = false
        }

        if ( name_contact_two.text.toString().isEmpty() ){
            name_contact_two.error = ""
            success = false
        }

        if ( phone_number_contact_two.text.toString().isEmpty() ){
            phone_number_contact_two.error = ""
            success = false
        }

        if ( rg_allergies.checkedRadioButtonId == -1 ){
            allergies.error = ""
            success = false
        } else {
            if ( rb_allergies_true.isChecked ){
                if ( input_allergies.text.toString().isEmpty() ){
                    input_allergies.error = "Introduce a que eres alergico(a)"
                    success = false
                }
            }
        }

        if ( spinner_alcohol.selectedItemPosition == 0 ) {
            Toast.makeText( requireContext(), "Selecciona tu consumo de alcohol", Toast.LENGTH_SHORT).show()
            success = false
        }

        if ( spinner_tobacco.selectedItemPosition == 0 ) {
            Toast.makeText( requireContext(), "Selecciona tu consumo de tabaco", Toast.LENGTH_SHORT).show()
            success = false
        }

        if ( spinner_drugs.selectedItemPosition == 0 ) {
            Toast.makeText( requireContext(), "Selecciona tu consumo de drogas", Toast.LENGTH_SHORT).show()
            success = false
        }

        if ( rg_respiratory.checkedRadioButtonId == -1 ){
            respiratory.error = ""
            success = false
        }

        if ( rg_gastrointestinal.checkedRadioButtonId == -1 ){
            gastrointestinal.error = ""
            success = false
        }

        if ( rg_nephrourological.checkedRadioButtonId == -1 ){
            nephrourological.error = ""
            success = false
        }

        if ( rg_neurological.checkedRadioButtonId == -1 ){
            neurological.error = ""
            success = false
        }

        if ( rg_hematological.checkedRadioButtonId == -1 ){
            hematological.error = ""
            success = false
        }

        if ( rg_gynecological.checkedRadioButtonId == -1 ){
            gynecological.error = ""
            success = false
        }

        if ( rg_infectious.checkedRadioButtonId == -1 ){
            infectious.error = ""
            success = false
        }

        if ( rg_endocrinological.checkedRadioButtonId == -1 ){
            endocrinological.error = ""
            success = false
        }

        if ( rg_surgical.checkedRadioButtonId == -1 ){
            surgical.error = ""
            success = false
        }

        if ( rg_traumatic.checkedRadioButtonId == -1 ){
            traumatic.error = ""
            success = false
        }

        if ( rg_medicines.checkedRadioButtonId == -1 ){
            medicines.error = ""
            success = false
        } else {
            if ( rb_medicines_true.isChecked ){
                if ( medicine_0.text.toString().isEmpty() ){
                    medicine_0.error = "Introduce el medicamento"
                    success = false
                }
                if ( units_0.text.toString().isEmpty() ){
                    units_0.error = "Introduce las unidades"
                    success = false
                }
                if ( lapse_0.text.toString().isEmpty() ){
                    lapse_0.error = "Lapso entre cada consumo"
                    success = false
                }
                if ( time_0.text.toString().isEmpty() ){
                    time_0.error = "Hora de ingerir medicamento"
                    success = false
                }
            }
        }

        return success
    }

    private fun fillData(userData: UserData) {
        val personalI: PersonalInformation = userData?.personalInformation!!
        val emergencyC: ArrayList<EmergencyContact> = ArrayList(userData.emergencyContacts)
        val allergies: String = userData.allergies
        val toxicHabits: ToxicHabits = userData.toxicHabits
        val diseases: Diseases = userData.diseases

        name.setText( personalI.name )
        mother_last_name.setText( personalI.motherLastName )
        father_last_name.setText( personalI.fatherLastName )
        weight.setText( personalI.weight.toString() )
        height.setText( personalI.height.toString() )

        birthDate = personalI.birthDate
        val index = personalI.birthDate.indexOf("/") + 1
        val lastIndex = personalI.birthDate.lastIndexOf("/")
        val month = personalI.birthDate.substring(index, lastIndex).toInt() + 1
        val date =  personalI.birthDate.replaceRange(index, lastIndex, month.toString())

        birth_date.setText( date )

        name_contact_one.setText( emergencyC[0].name)
        phone_number_contact_one.setText( emergencyC[0].phoneNumber)
        name_contact_two.setText( emergencyC[1].name)
        phone_number_contact_two.setText( emergencyC[1].phoneNumber)

        if ( allergies.isEmpty() ) {
            rb_allergies_false.isChecked = true
        } else {
            rb_allergies_true.isChecked = true
            input_allergies.setText( allergies )
            container_input_allergies.visibility = View.VISIBLE
        }

        spinner_sex.setSelection( if (personalI.sex.equals("Femenino")) 1 else 2 )

        when ( personalI.bloodType ) {
            "O+" -> spinner_blood_types.setSelection(1)
            "O-" -> spinner_blood_types.setSelection(2)
            "A+" -> spinner_blood_types.setSelection(3)
            "A-" -> spinner_blood_types.setSelection(4)
            "B+" -> spinner_blood_types.setSelection(5)
            "B-" -> spinner_blood_types.setSelection(6)
            "AB+" -> spinner_blood_types.setSelection(7)
            "AB-" -> spinner_blood_types.setSelection(8)
        }

        when ( toxicHabits.alcohol ) {
            "Nunca" -> spinner_alcohol.setSelection(1)
            "Ocasionalmente" -> spinner_alcohol.setSelection(2)
            "Generalmente" -> spinner_alcohol.setSelection(3)
            "Siempre" -> spinner_alcohol.setSelection(4)
        }

        when ( toxicHabits.tobacco ) {
            "Nunca" -> spinner_tobacco.setSelection(1)
            "Ocasionalmente" -> spinner_tobacco.setSelection(2)
            "Generalmente" -> spinner_tobacco.setSelection(3)
            "Siempre" -> spinner_tobacco.setSelection(4)
        }

        when ( toxicHabits.drugs ) {
            "Nunca" -> spinner_drugs.setSelection(1)
            "Ocasionalmente" -> spinner_drugs.setSelection(2)
            "Generalmente" -> spinner_drugs.setSelection(3)
            "Siempre" -> spinner_drugs.setSelection(4)
        }

        if ( diseases.respiratory.equals("Si") ) {
            rb_respiratory_true.isChecked = true
        } else {
            rb_respiratory_false.isChecked = true
        }

        if ( diseases.gastrointestinal.equals("Si") ) {
            rb_gastrointestinal_true.isChecked = true
        } else {
            rb_gastrointestinal_false.isChecked = true
        }

        if ( diseases.nephrourological.equals("Si") ) {
            rb_nephrourological_true.isChecked = true
        } else {
            rb_nephrourological_false.isChecked = true
        }

        if ( diseases.neurological.equals("Si") ) {
            rb_neurological_true.isChecked = true
        } else {
            rb_neurological_false.isChecked = true
        }

        if ( diseases.hematological.equals("Si") ) {
            rb_hematological_true.isChecked = true
        } else {
            rb_hematological_false.isChecked = true
        }

        if ( diseases.gynecological.equals("Si") ) {
            rb_gynecological_true.isChecked = true
        } else {
            rb_gynecological_false.isChecked = true
        }

        if ( diseases.infectious.equals("Si") ) {
            rb_infectious_true.isChecked = true
        } else {
            rb_infectious_false.isChecked = true
        }

        if ( diseases.endocrinological.equals("Si") ) {
            rb_endocrinological_true.isChecked = true
        } else {
            rb_endocrinological_false.isChecked = true
        }

        if ( diseases.surgical.equals("Si") ) {
            rb_surgical_true.isChecked = true
        } else {
            rb_surgical_false.isChecked = true
        }

        if ( diseases.traumatic.equals("Si") ) {
            rb_traumatic_true.isChecked = true
        } else {
            rb_traumatic_false.isChecked = true
        }

        if ( userData.medicines.isEmpty() ) {
            rb_medicines_false.isChecked = true
        } else {
            rb_medicines_true.isChecked = true
            container_medicines.visibility = View.VISIBLE

            medicine_0.setText( userData.medicines[0].name )
            units_0.setText( userData.medicines[0].units )
            lapse_0.setText( userData.medicines[0].lapse )
            time_0.setText( userData.medicines[0].time )

            medicine_1.setText( userData.medicines[1].name )
            units_1.setText( userData.medicines[1].units )
            lapse_1.setText( userData.medicines[1].lapse )
            time_1.setText( userData.medicines[1].time )

        }
    }

}