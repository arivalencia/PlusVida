package com.life.pluslife

import android.content.Context
import androidx.appcompat.app.AlertDialog

class Tools {

    companion object {

        fun alertDialog(
            context: Context?,
            title: String?,
            message: String?,
            titleOK: String?,
            negativeButton: () -> Unit,
            positiveButton: () -> Unit
        ) {
            AlertDialog.Builder(context!!)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton( "Cancelar" ) { dialogInterface, i -> negativeButton() }
                .setPositiveButton( titleOK ) { dialogInterface, i -> positiveButton() }
                .show()
        }

    }
}