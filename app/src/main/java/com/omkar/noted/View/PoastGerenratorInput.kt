package com.omkar.noted.View

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.omkar.noted.R

class PoastGerenratorInput : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuImage: ImageView
    private lateinit var navigationView: NavigationView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poast_gerenrator_input)

        menuIcon = findViewById(R.id.menu_icon)

        menuIcon.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }
    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_dwawer, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_account -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.saved_poast -> {
                    handleLogout()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun handleLogout() {
//        FirebaseAuth.getInstance().signOut()
//        // Navigate to login
//        startActivity(Intent(this, LoginActivity::class.java))
//        finish()
    }
}