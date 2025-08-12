package com.o7solutions.braingames

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnRequestOtp: Button
    private lateinit var layoutNewPassword: LinearLayout
    private lateinit var etNewPassword: EditText
//    private lateinit var btnChangePassword: Button

    private val generatedOtp = "1234"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        etEmail = findViewById(R.id.etEmail)
        btnRequestOtp = findViewById(R.id.btnRequestOtp)
        layoutNewPassword = findViewById(R.id.layoutNewPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
//        btnChangePassword = findViewById(R.id.btnChangePassword)

        btnRequestOtp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Here you'd actually send OTP to the email
            Toast.makeText(this, "OTP sent to $email", Toast.LENGTH_SHORT).show()

            // Show OTP dialog
            showOtpDialog()
        }

//        btnChangePassword.setOnClickListener {
//            val newPass = etNewPassword.text.toString().trim()
//            if (newPass.isEmpty()) {
//                Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
//        }
    }


    private fun showOtpDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.otp_dialog, null)
        val etOtp = dialogView.findViewById<EditText>(R.id.etOtp)

        AlertDialog.Builder(this)
            .setTitle("Enter OTP")
            .setView(dialogView)
            .setPositiveButton("Verify") { dialog, _ ->
                val enteredOtp = etOtp.text.toString().trim()
                if (enteredOtp == generatedOtp) {
                    Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()
                    layoutNewPassword.visibility = LinearLayout.VISIBLE
                } else {
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}