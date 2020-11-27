package com.life.pluslife.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.life.pluslife.Constants
import com.life.pluslife.R
import com.life.pluslife.activities.ScanQRActivity
import com.life.pluslife.helpers.LocalHelper
import kotlinx.android.synthetic.main.fragment_help.*

class HelpFragment: Fragment() {

    private val TAG: String = "HelpFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = LocalHelper( requireContext() ).getUser()

        help_someone.setOnClickListener {
            startActivity( Intent( context, ScanQRActivity::class.java) )
        }

        if ( user != null ){
            if ( user.data != null ){
                qr_code.setImageBitmap( generateQRCode( user.email ))

                qr_code.setOnClickListener {
                    val intentBrowser = Intent(Intent.ACTION_VIEW)
                    intentBrowser.data = Uri.parse(Constants.BASE_URL + user.email)
                    startActivity(intentBrowser)
                }
            } else {
                noData()
            }
        } else {
            noData()
        }

    }

    private fun generateQRCode(email: String): Bitmap {
        val url = "${Constants.BASE_URL}$email"
        val width = 600
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(url, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Toast.makeText( requireContext(), "Error al generar su codigo QR", Toast.LENGTH_SHORT)
                .show()
            Log.d(TAG, "generateQRCode error: ${e.message}")
        }
        return bitmap
    }

    private fun noData() {
        qr_code.visibility = View.GONE
        text_your_code.text = "Llena tus datos para generar tu codigo QR"
    }
}