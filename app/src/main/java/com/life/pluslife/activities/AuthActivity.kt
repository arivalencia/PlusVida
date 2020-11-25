package com.life.pluslife.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.life.pluslife.Constants
import com.life.pluslife.MainActivity
import com.life.pluslife.R
import com.life.pluslife.Tools
import com.life.pluslife.helpers.LocalHelper
import com.life.pluslife.pojos.User
import kotlinx.android.synthetic.main.activity_auth.*


class AuthActivity : AppCompatActivity() {

    private var TAG = "AuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        save.setOnClickListener {

            val email = email_input.text.toString()
            val pass = password_input.text.toString()

            if ( email.isNotEmpty() ){
                if ( pass.length > 5 ){
                    if ( isValidEmail( email ) ){

                        if ( change_action.text.toString().equals( "Registrarme" )){
                            logIn( email, pass )
                        } else {
                            signIn( email, pass )
                        }

                    } else {
                        email_input.error = "Email invalido"
                    }
                } else{
                    password_input.error = "La contraseña debe tener mínimo 6 caracteres"
                }
            } else {
                email_input.error = "Se necesita un email"
            }

        }

        button_google.setOnClickListener {
            val googleConf =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken( getString( R.string.default_web_client_id) )
                    .requestEmail()
                    .build()
            val googleClient = GoogleSignIn.getClient( this, googleConf)
            googleClient.signOut()

            startActivityForResult( googleClient.signInIntent, Constants.GOOGLE_SIGN_IN)
        }

        change_action.setOnClickListener {
            if ( change_action.text.toString().equals( "Registrarme" )){
                text_action.text = "Registrarse"
                save.text = "Registrarme"
                change_action.text = "Iniciar sesión"
            } else {
                text_action.text = "Iniciar sesión"
                save.text = "Ingresar"
                change_action.text = "Registrarme"
            }
            email_input.setText("")
            password_input.setText("")
        }
    }

    /*
     * INICIAR SESIÓN
     */
    private fun logIn(email: String, pass: String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword( email, pass )
            .addOnCompleteListener{
                if ( it.isSuccessful ){

                    val user = User(email, null)

                    LocalHelper(this).setUser(user)
                    startActivity( Intent(this, MainActivity::class.java) )
                    finish()
                } else {
                    Tools.alertDialog(
                        this,
                        "Error al iniciar sesión",
                        it.exception?.message,
                        "OK",
                        {},
                        {}
                    )
                }
            }
    }

    /*
     * REGISTRARSE
     */
    private fun signIn(email: String, pass: String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword( email, pass )
            .addOnCompleteListener{
                if ( it.isSuccessful ){

                    val user = User(email, null)

                    LocalHelper(this).setUser(user)
                    startActivity( Intent(this, MainActivity::class.java) )
                    finish()
                } else {
                    Tools.alertDialog(
                        this,
                        "Error al registrarse",
                        it.exception?.message.toString(),
                        "OK",
                        {},
                        {}
                    )
                }
            }
    }

    fun isValidEmail(email: CharSequence?): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ( requestCode == Constants.GOOGLE_SIGN_IN){

            val task = GoogleSignIn.getSignedInAccountFromIntent( data )

            try {
                val account = task.getResult( ApiException::class.java )!!

                if ( account != null){

                    val credential = GoogleAuthProvider.getCredential( account.idToken, null )

                    FirebaseAuth.getInstance().signInWithCredential( credential )
                        .addOnCompleteListener{
                            if ( it.isSuccessful ){

                                val user = User(account.email + "", null)
                                LocalHelper(this).setUser(user)
                                startActivity( Intent(this, MainActivity::class.java) )
                                finish()

                            } else {
                                Tools.alertDialog(
                                    this,
                                    "Error al ingresar con Google",
                                    it.exception?.message.toString(),
                                    "OK", {}, {}
                                )
                            }
                        }
                }
            } catch ( e: ApiException){
                Tools.alertDialog(
                    this,
                    "Error al ingresar con Google",
                    e.message,
                    "OK", {}, {}
                )
            }
        }

    }
}