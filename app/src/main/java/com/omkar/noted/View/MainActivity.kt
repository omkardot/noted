package com.omkar.noted.View

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.omkar.noted.Fragements.LoginFragment
import com.omkar.noted.Fragements.ProfileFragment
import com.omkar.noted.Fragements.SavedPostFragment
import com.omkar.noted.Fragements.SettingFragment
import com.omkar.noted.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        fetchIntent()
    }
    fun fetchIntent(){
        val intent = intent
        val from =intent.getStringExtra("from")
        Log.d("form",from.toString())
        if (from.equals("profile")){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }
        else if (from.equals("settings")){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingFragment())
                .commit()
        }
        else if (from.equals("saved")){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SavedPostFragment())
                .commit()
        }
        else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }


    }
}
