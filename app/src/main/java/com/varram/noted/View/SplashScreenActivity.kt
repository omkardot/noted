package com.varram.noted.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.varram.noted.R

import android.animation.ObjectAnimator
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import com.varram.noted.Genaric.NotedSharedPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var prefs: NotedSharedPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        prefs = NotedSharedPreference(this@SplashActivity)

        // Find the views initialized in activity_splash.xml
        val logoIcon = findViewById<View>(R.id.logo_icon)
        val appNameText = findViewById<View>(R.id.app_name_text)
        val taglineText = findViewById<View>(R.id.tagline_text)

        // Coroutines allow us to sequence the animations with clean delays
        lifecycleScope.launch {
            // --- Phase 1: Logo Pop-In Animation (0.7s duration) ---

            // 1. Logo Pop (Scale up, Rotate to 0, and Fade in)
            logoIcon.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .rotation(0f)
                .alpha(1.0f)
                .setDuration(700)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            // 2. Logo Glow/Shine Effect (Quick alpha pulse for flair)
            val glowAnimator = ObjectAnimator.ofFloat(logoIcon, "alpha", 1.0f, 0.8f, 1.0f)
            glowAnimator.duration = 400
            glowAnimator.startDelay = 300 // Start the pulse as the pop is settling
            glowAnimator.start()

            // Wait for logo animation to largely settle (800ms)
            delay(1000)

            // --- Phase 2: Text Fade-Up Cascade Animation (0.5s duration) ---

            // 1. App Name Fade Up
            appNameText.animate()
                .alpha(1.0f)
                .translationY(0f) // Move up to final position (0dp translation)
                .setDuration(1000)
                .start()

            // Slight delay for the cascading effect
            delay(200)

            // 2. Tagline Fade Up
            taglineText.animate()
                .alpha(0.75f) // Slightly lower alpha for the tagline
                .translationY(0f)
                .setDuration(500)
                .start()

            // Wait for the animation to finish and display time (1000ms)
            delay(2500)

            val isLoggedIn = prefs.getBoolean("isLoggedIn")?:false
            Log.d("logedin",isLoggedIn.toString())
            if (isLoggedIn) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                intent.putExtra("navigate_to", "home")
                startActivity(intent)
            } else {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                intent.putExtra("navigate_to", "login")
                startActivity(intent)
            }
            finish()
        }
    }
}
