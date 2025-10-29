package com.omkar.noted.View

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
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
    private lateinit var input_text_area: EditText
    private var selectedTone = "professional"
    private var selectedlength = "short"
    private var includeHashtag = false
    private lateinit var ll_profetional:LinearLayout
    private lateinit var ll_friendly:LinearLayout
    private lateinit var ll_storytelling:LinearLayout
    private lateinit var tv_profetional:TextView
    private lateinit var tv_friendly:TextView
    private lateinit var tv_storytelling:TextView
    private lateinit var ll_short:LinearLayout
    private lateinit var ll_medium:LinearLayout
    private lateinit var ll_long:LinearLayout
    private lateinit var tv_short:TextView
    private lateinit var tv_medium:TextView
    private lateinit var tv_long:TextView
    private lateinit var switch: SwitchCompat
    private lateinit var ll_generate_btn: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var textStatus: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poast_gerenrator_input)
        iniatView()
        onclickListners()


        menuIcon.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun onclickListners() {
        ll_profetional.setOnClickListener {
           selectedTone = "professional"
            ll_storytelling.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
           ll_friendly.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
           ll_profetional.background = ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
           tv_profetional.setTextColor(resources.getColor(R.color.white))
           tv_friendly.setTextColor(resources.getColor(R.color.text_secondary))
           tv_storytelling.setTextColor(resources.getColor(R.color.text_secondary))
        }
        ll_friendly.setOnClickListener {
            selectedTone = "friendly"
            ll_storytelling.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_friendly.background = ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            ll_profetional.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            tv_profetional.setTextColor(resources.getColor(R.color.text_secondary))
            tv_friendly.setTextColor(resources.getColor(R.color.white))
            tv_storytelling.setTextColor(resources.getColor(R.color.text_secondary))

        }
        ll_storytelling.setOnClickListener {
            selectedTone = "story"
            ll_storytelling.background = ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            ll_friendly.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_profetional.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            tv_profetional.setTextColor(resources.getColor(R.color.text_secondary))
            tv_friendly.setTextColor(resources.getColor(R.color.text_secondary))
            tv_storytelling.setTextColor(resources.getColor(R.color.white))
        }
        ll_short.setOnClickListener {
            selectedlength = "short"
            ll_long.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_medium.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_short.background = ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            tv_short.setTextColor(resources.getColor(R.color.white))
            tv_medium.setTextColor(resources.getColor(R.color.text_secondary))
            tv_long.setTextColor(resources.getColor(R.color.text_secondary))
        }
        ll_medium.setOnClickListener {
            selectedlength = "medium"
            ll_long.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_medium.background = ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            ll_short.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            tv_long.setTextColor(resources.getColor(R.color.text_secondary))
            tv_medium.setTextColor(resources.getColor(R.color.white))
            tv_short.setTextColor(resources.getColor(R.color.text_secondary))

        }
        ll_long.setOnClickListener {
            selectedlength = "long"
            ll_long.background = ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            ll_short.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_medium.background = ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            tv_medium.setTextColor(resources.getColor(R.color.text_secondary))
            tv_short.setTextColor(resources.getColor(R.color.text_secondary))
            tv_long.setTextColor(resources.getColor(R.color.white))
        }
        if (switch.isChecked){
            includeHashtag = true
        }
        else{
            includeHashtag = false
        }
        ll_generate_btn.setOnClickListener {
            callgeminiapi()
        }

    }
    fun callgeminiapi(){
        showLoading()
// simulate loading...
        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
        }, 2000)

    }
    fun showLoading() {
        progressBar.visibility = View.VISIBLE
        textStatus.text = "Crafting your post"
        ll_generate_btn.alpha =0.5f
    }

    // Hide it when done
    fun hideLoading() {
        progressBar.visibility = View.GONE
        textStatus.text = "Loaded successfully!"
        ll_generate_btn.alpha =1.0f
    }
    private fun iniatView(){
        menuIcon = findViewById(R.id.menu_icon)
        input_text_area = findViewById(R.id.input_text_area)
        ll_profetional = findViewById(R.id.ll_professional)
        ll_friendly = findViewById(R.id.ll_friendly)
        ll_storytelling = findViewById(R.id.ll_story_telling)
        tv_profetional = findViewById(R.id.tv_professional)
        tv_friendly = findViewById(R.id.tv_friendly)
        tv_storytelling = findViewById(R.id.tv_story_telling)
        ll_short = findViewById(R.id.ll_short)
        ll_medium = findViewById(R.id.ll_medium)
        ll_long = findViewById(R.id.ll_long)
        tv_short = findViewById(R.id.tv_short)
        tv_medium = findViewById(R.id.tv_medium)
        tv_long = findViewById(R.id.tv_long)
        switch = findViewById(R.id.switch_hashtags)
        ll_generate_btn = findViewById(R.id.ll_generate_btn)
        textStatus = findViewById(R.id.text_status)
        progressBar = findViewById(R.id.progressBar)
    }
    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_dwawer, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_account -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PoastGerenratorInput,MainActivity::class.java)
                    intent.putExtra("from","profile")
                    startActivity(intent)
                    true
                }
                R.id.menu_settings -> {
                    val intent = Intent(this@PoastGerenratorInput,MainActivity::class.java)
                    intent.putExtra("from","settings")
                    startActivity(intent)
                    true
                }
                R.id.saved_poast -> {
                    val intent = Intent(this@PoastGerenratorInput,MainActivity::class.java)
                    intent.putExtra("from","saved")
                    startActivity(intent)
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