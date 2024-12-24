package com.millenialzdev.uasdaa.presentation.page.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harshita.sliderapplication.adapter.IntroAdapter
import com.millenialzdev.uasdaa.OptimasiBiaya
import com.millenialzdev.uasdaa.R

class IntroActivity : AppCompatActivity() {
    companion object {
        lateinit var firebaseAuth: FirebaseAuth
        lateinit var firestore: FirebaseFirestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        supportActionBar?.hide()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if user is already logged in
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is signed in, redirect to home
            navigateToHome()
            return
        }

        val introPager = findViewById<ViewPager2>(R.id.introPager)

        val fragmentList = arrayListOf<Fragment>(
            IntroFirst(),
            IntroSecond(),
            IntroThird()
        )

        val adapter = IntroAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )

        introPager.adapter = adapter
    }

    private fun navigateToHome() {
        val toHome = Intent(this, OptimasiBiaya::class.java)
        toHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(toHome)
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in when the activity starts
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            navigateToHome()
        }
    }
}