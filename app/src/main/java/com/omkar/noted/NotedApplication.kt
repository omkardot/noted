package com.omkar.noted

import android.app.Application
import com.google.firebase.FirebaseApp

class NotedApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}