package com.omkar.noted.Fragements

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.omkar.noted.Database.DatabaseHelper
import com.omkar.noted.R
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : AppCompatActivity() {
    private lateinit var db:DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        db = DatabaseHelper(this@ProfileFragment)
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

        }
    }
    private fun fetchNameAndImageFromDB(profile_name:TextView,profile_image: CircleImageView) {
        val (name, imageUrl) = db.getImageAndName()
        profile_name.text = name.toString()
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .placeholder(R.drawable.baseline_person_24)
            .into(profile_image)
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