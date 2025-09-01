package com.o7solutions.braingames.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.o7solutions.braingames.R

object NetworkDialogHelper {
    private var dialog: AlertDialog? = null

    fun showNoInternetDialog(context: Context) {
        val activity = context as? Activity ?: return // only proceed if context is Activity

        if (dialog == null || dialog?.isShowing == false) {
            val builder = AlertDialog.Builder(activity)
                .setTitle("No Internet Connection")
                .setMessage("Please check your network settings.")
                .setCancelable(false) // user cannot dismiss
//                .setPositiveButton("Retry") { _, _ ->
//                    dialog?.dismiss()
//                }

            dialog = builder.create()
            dialog?.show()

            dialog?.findViewById<TextView>(android.R.id.message)?.setTextColor(Color.BLACK)
            dialog?.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)?.setTextColor(Color.BLACK)
        }
    }

    fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }
}