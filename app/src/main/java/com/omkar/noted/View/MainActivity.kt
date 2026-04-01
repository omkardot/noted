package com.omkar.noted.View

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.omkar.noted.Database.DatabaseHelper
import com.omkar.noted.Fragements.LoginFragment
import com.omkar.noted.Fragements.PoastGeneratorInputFragment
import com.omkar.noted.Fragements.ProfileFragment
import com.omkar.noted.Fragements.SavedPostFragment
import com.omkar.noted.Fragements.SettingFragment
import com.omkar.noted.Genaric.NotedSharedPreference
import com.omkar.noted.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: LinearLayout
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefs: NotedSharedPreference

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedMethod()
        }
    }

    private fun onBackPressedMethod() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)







        dbHelper = DatabaseHelper(this@MainActivity)
        prefs = NotedSharedPreference(this@MainActivity)
        drawerLayout = findViewById(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menu_icon)
        toolbar = findViewById<LinearLayout>(R.id.toolbar)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.itemIconTintList = null
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val drawerHeader = navigationView.getHeaderView(0)
        val username = drawerHeader.findViewById<TextView>(R.id.usrname)
        val email = drawerHeader.findViewById<TextView>(R.id.usremail)
        val profile_image = drawerHeader.findViewById<CircleImageView>(R.id.profileImg)

        val userdata = dbHelper.getImageAndName()
        username.setText(userdata.first)
        val emailtext = dbHelper.getAllUsers()
        email.text = emailtext.get(0).email

        Glide.with(this@MainActivity)
            .asBitmap()
            .load(userdata.second)
            .placeholder(R.drawable.profilepicture)
            .into(profile_image)
        // DEFAULT FRAGMENT
        replaceFragment(PoastGeneratorInputFragment())


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = false

        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this@MainActivity, ProfileFragment::class.java))
            }

            R.id.nav_help -> {
                showHelpAndSupportDialog()
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.nav_savedPost -> {
                startActivity(Intent(this@MainActivity, SavedPostFragment::class.java))
            }

            R.id.nav_logout -> {
                showlogoutDialog()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showlogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)
        val btnCancel = dialogView.findViewById<LinearLayout>(R.id.cancel)
        val btnlogout = dialogView.findViewById<LinearLayout>(R.id.btnlogout)

        btnlogout.setOnClickListener {
            logoutFromApp()
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun logoutFromApp() {
        Log.d(TAG, "logoutFromApp:before "+prefs.getBoolean("isLoggedIn"))
        if (prefs.getBoolean("isLoggedIn")){
            prefs.saveBoolean("isLoggedIn",false)
            Log.d(TAG, "logoutFromApp:inside "+prefs.getBoolean("isLoggedIn"))
        }
        Log.d(TAG, "logoutFromApp:outside "+prefs.getBoolean("isLoggedIn"))

        lifecycleScope.launch(Dispatchers.IO) {
            dbHelper.clearAllTables()
            withContext(Dispatchers.Main) {
                Log.d(TAG, "logoutFromApp: "+prefs.getBoolean("isLoggedIn"))
                    Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity,SplashActivity::class.java))
            }
        }
    }
    private fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
    private fun showHelpAndSupportDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_help_support, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Initialize views
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)
        val btnContactSupport = dialogView.findViewById<LinearLayout>(R.id.btnContactSupport)
        val btnReportBug = dialogView.findViewById<LinearLayout>(R.id.btnReportBug)

        // Set click listeners
        btnClose.setOnClickListener {
            dialog.dismiss()
        }


        btnContactSupport.setOnClickListener {
            dialog.dismiss()
            openContactSupport()
        }
        btnReportBug.setOnClickListener {
            dialog.dismiss()
            openReportBug()
        }

        // Show dialog with rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    // Helper functions
    private fun openFAQSection() {
        // Navigate to FAQ screen or open URL
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yourapp.com/faq"))
        startActivity(intent)
    }

    private fun openContactSupport() {
        // Open email client or support chat
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@Noted.com")
            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
        }
        startActivity(Intent.createChooser(intent, "Contact Support"))
    }

    private fun openReportBug() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:bugs@noted.com")
            putExtra(Intent.EXTRA_SUBJECT, "Bug Report")
        }
        startActivity(Intent.createChooser(intent, "Report Bug"))
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
