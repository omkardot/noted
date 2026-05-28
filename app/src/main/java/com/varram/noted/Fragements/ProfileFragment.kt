package com.varram.noted.Fragements

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.varram.noted.Database.DatabaseHelper
import com.varram.noted.Genaric.NotedSharedPreference
import com.varram.noted.R
import com.varram.noted.View.SplashActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : AppCompatActivity() {
    private lateinit var db:DatabaseHelper

    private lateinit var prefs: NotedSharedPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        db = DatabaseHelper(this@ProfileFragment)
        prefs = NotedSharedPreference(this@ProfileFragment)
        initView()
    }


    private fun initView() {
        val actionback = findViewById<ImageView>(R.id.action_back)
        val profileImage = findViewById<CircleImageView>(R.id.profileImage)
        val profile_name = findViewById<TextView>(R.id.profile_name)
        val ll_helpandsupport = findViewById<LinearLayout>(R.id.ll_helpandsupport)
        val logout = findViewById<TextView>(R.id.logout)

        actionback.setOnClickListener {
        onBackPressed()
        }



        fetchNameAndImageFromDB(profile_name,profileImage)
        ll_helpandsupport.setOnClickListener {
            showHelpAndSupportDialog()
        }
        logout.setOnClickListener {
            showlogoutDialog()
        }
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
    private fun fetchNameAndImageFromDB(profile_name:TextView,profile_image: CircleImageView) {
        val (name, imageUrl) = db.getImageAndName()
        profile_name.text = name.toString()
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .placeholder(R.drawable.profilepicture)
            .into(profile_image)
    }
    private fun logoutFromApp() {
        Log.d(TAG, "logoutFromApp:before "+prefs.getBoolean("isLoggedIn"))
        if (prefs.getBoolean("isLoggedIn")){
            prefs.saveBoolean("isLoggedIn",false)
            Log.d(TAG, "logoutFromApp:inside "+prefs.getBoolean("isLoggedIn"))
        }
        Log.d(TAG, "logoutFromApp:outside "+prefs.getBoolean("isLoggedIn"))

        lifecycleScope.launch(Dispatchers.IO) {
            db.clearAllTables()
            withContext(Dispatchers.Main) {
                Log.d(TAG, "logoutFromApp: "+prefs.getBoolean("isLoggedIn"))
                Toast.makeText(this@ProfileFragment, "Logged out successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@ProfileFragment, SplashActivity::class.java))
            }
        }
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
}