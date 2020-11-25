package com.life.pluslife.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.life.pluslife.MainActivity
import com.life.pluslife.R
import com.life.pluslife.helpers.LocalHelper
import com.life.pluslife.pojos.User

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val localHelper = LocalHelper(this)

        if ( localHelper.getUser() != null ){
            if ( localHelper.getUser()?.email != null ) {
                startActivity( Intent(this, MainActivity::class.java) )
            } else {
                startActivity( Intent(this, AuthActivity::class.java) )
            }
        } else {
            startActivity( Intent(this, AuthActivity::class.java) )
        }

        finish()

    }
}