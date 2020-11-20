package com.life.pluslife.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.life.pluslife.R
import com.life.pluslife.activities.ScanQRActivity
import kotlinx.android.synthetic.main.fragment_help.*

class HelpFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        help_someone.setOnClickListener {
            startActivity( Intent( context, ScanQRActivity::class.java) )
        }

    }
}