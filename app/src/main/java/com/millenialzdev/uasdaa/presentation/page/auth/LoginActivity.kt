package com.millenialzdev.uasdaa.presentation.page.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.millenialzdev.uasdaa.OptimasiBiaya
import com.millenialzdev.uasdaa.R
import com.millenialzdev.uasdaa.presentation.page.intro.IntroActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication
        auth = IntroActivity.firebaseAuth

        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, go to home activity
            navigateToHome()
            return
        }

        // redirect ke home
        val toHome = Intent(this, OptimasiBiaya::class.java)
        toHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // redirect ke register
        val toRegister = Intent(this, RegisterActivity::class.java)
        toRegister.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // trigger redirect ke register
        val buttonToRegister = findViewById<TextView>(R.id.toRegister)
        buttonToRegister.setOnClickListener {
            startActivity(toRegister)
        }

        // form input login
        val inpEmail = findViewById<EditText>(R.id.inputEmail)
        val inpPass = findViewById<EditText>(R.id.inputPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        // trigger login
        buttonLogin.setOnClickListener {
            val email = inpEmail.text.toString().trim()
            val password = inpPass.text.toString().trim()

            // Validate inputs
            if (validateInputs(email, password)) {
                performLogin(email, password)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                Toast.makeText(this, "Mohon masukkan email", Toast.LENGTH_SHORT).show()
                return false
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Mohon masukkan password", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun performLogin(email: String, password: String) {
        // Show loading indicator if you have one

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = auth.currentUser
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            // Get and store the Firebase ID token
                            val idToken = tokenTask.result?.token

                            // Save token to SharedPreferences or secure storage
                            saveFirebaseToken(idToken)

                            // Navigate to home
                            navigateToHome()
                        } else {
                            // Handle token retrieval error
                            Toast.makeText(
                                this,
                                "Gagal mendapatkan token: ${tokenTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Login failed
                    Toast.makeText(
                        this,
                        "Login gagal: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveFirebaseToken(token: String?) {
        token?.let {
            // Option 1: Save to SharedPreferences (less secure)
            val pref = getSharedPreferences("AppPref", MODE_PRIVATE)
            pref.edit().putString("FIREBASE_TOKEN", it).apply()

            // Option 2: Recommended - Use EncryptedSharedPreferences
            // val pref = EncryptedSharedPreferences.create(...)
            // pref.edit().putString("FIREBASE_TOKEN", it).apply()
        }
    }

    private fun navigateToHome() {
        val toHome = Intent(this, OptimasiBiaya::class.java)
        toHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(toHome)
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHome()
        }
    }
}