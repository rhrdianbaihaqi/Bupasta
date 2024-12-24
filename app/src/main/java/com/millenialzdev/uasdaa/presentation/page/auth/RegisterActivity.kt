package com.millenialzdev.uasdaa.presentation.page.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.millenialzdev.uasdaa.OptimasiBiaya
import com.millenialzdev.uasdaa.R
import com.millenialzdev.uasdaa.presentation.page.intro.IntroActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Firestore
        auth = IntroActivity.firebaseAuth
        firestore = FirebaseFirestore.getInstance()

        // Navigate to login page
        val toLogin = Intent(this, LoginActivity::class.java)
        toLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val buttonToLogin = findViewById<TextView>(R.id.toLogin)
        buttonToLogin.setOnClickListener {
            startActivity(toLogin)
        }

        // Input form registration
        val inpName = findViewById<EditText>(R.id.inputNama)
        val inpNoHp = findViewById<EditText>(R.id.inputTelp)
        val inpEmail = findViewById<EditText>(R.id.inputEmail)
        val inpPass = findViewById<EditText>(R.id.inputPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        // Register button click listener
        buttonRegister.setOnClickListener {
            // Validate inputs
            val name = inpName.text.toString().trim()
            val phone = inpNoHp.text.toString().trim()
            val email = inpEmail.text.toString().trim()
            val password = inpPass.text.toString().trim()

            // Input validation
            if (validateInputs(name, phone, email, password)) {
                registerUser(name, phone, email, password)
            }
        }
    }

    private fun validateInputs(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Boolean {
        when {
            name.isEmpty() -> {
                Toast.makeText(this, "Mohon masukkan nama", Toast.LENGTH_SHORT).show()
                return false
            }
            phone.isEmpty() -> {
                Toast.makeText(this, "Mohon masukkan no telepon", Toast.LENGTH_SHORT).show()
                return false
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Mohon masukkan Email", Toast.LENGTH_SHORT).show()
                return false
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Mohon masukkan password", Toast.LENGTH_SHORT).show()
                return false
            }
            password.length < 6 -> {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun registerUser(name: String, phone: String, email: String, password: String) {
        // Show loading indicator if you have one

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful, save additional user info to Firestore
                    val currentUser = auth.currentUser
                    val userMap = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "email" to email
                    )
                    currentUser?.let { user ->
                        firestore.collection("users")
                            .document(user.uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                // User data saved successfully
                                Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                                // SignOut Ke Login
                                auth.signOut()
//                                // Navigate to login
                                val toLogin = Intent(this, LoginActivity::class.java)
                                toLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(toLogin)
                            }
                            .addOnFailureListener { e ->
                                // Handle firestore save error
                                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Registration failed
                    Toast.makeText(
                        this,
                        "Registrasi gagal: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}