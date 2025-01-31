package com.life.pluslife.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.life.pluslife.R
import com.life.pluslife.Tools
import com.life.pluslife.activities.AuthActivity
import com.life.pluslife.helpers.LocalHelper
import com.life.pluslife.pojos.User
import kotlinx.android.synthetic.main.fragment_options.*

class OptionsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        help.setOnClickListener {
            Toast.makeText( requireContext(), "Module in development", Toast.LENGTH_SHORT).show()
        }

        log_out.setOnClickListener {
            Tools.alertDialog(
                context,
                "Cerrar sesión",
                "¿Esta seguro que desea cerrar sesión",
                "Salir",
                { /*PROCESS NEGATIVE BUTTON*/ },
                {
                    /*PROCESS POSITIVE BUTTON*/
                    FirebaseAuth.getInstance().signOut()

                    val localHelper = context?.let { it1 -> LocalHelper(it1) }
                    localHelper?.setUser(null)

                    activity?.finish()
                    startActivity( Intent(context, AuthActivity::class.java) )
                }
            )
        }
    }
}