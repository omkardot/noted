package com.omkar.noted.View

import HuggingFaceTextGenerator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.omkar.noted.Genaric.Content
import com.omkar.noted.Genaric.GeminiApiService
import com.omkar.noted.Genaric.GeminiRequest
import com.omkar.noted.Genaric.GenricApiCalls
import com.omkar.noted.Genaric.Part
import com.omkar.noted.R
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

class PoastGerenratorInput : AppCompatActivity() {
    private lateinit var generator : HuggingFaceTextGenerator

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuImage: ImageView
    private lateinit var navigationView: NavigationView
    private lateinit var menuIcon: ImageView
    private lateinit var input_text_area: EditText
    private var selectedTone = "professional"
    private var selectedlength = "short"
    private var includeHashtag = "Yes"
    private lateinit var ll_profetional: LinearLayout
    private lateinit var ll_friendly: LinearLayout
    private lateinit var ll_storytelling: LinearLayout
    private lateinit var tv_profetional: TextView
    private lateinit var tv_friendly: TextView
    private lateinit var tv_storytelling: TextView
    private lateinit var ll_short: LinearLayout
    private lateinit var ll_medium: LinearLayout
    private lateinit var ll_long: LinearLayout
    private lateinit var tv_short: TextView
    private lateinit var tv_medium: TextView
    private lateinit var tv_long: TextView
    private lateinit var switch: SwitchCompat
    private lateinit var ll_generate_btn: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var textStatus: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poast_gerenrator_input)
        iniatView()
        generator= HuggingFaceTextGenerator(this@PoastGerenratorInput)

        onclickListners()


        menuIcon.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun onclickListners() {
        ll_profetional.setOnClickListener {
            selectedTone = "professional"
            ll_storytelling.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_friendly.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_profetional.background =
                ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            tv_profetional.setTextColor(resources.getColor(R.color.white))
            tv_friendly.setTextColor(resources.getColor(R.color.text_secondary))
            tv_storytelling.setTextColor(resources.getColor(R.color.text_secondary))
        }
        ll_friendly.setOnClickListener {
            selectedTone = "friendly"
            ll_storytelling.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_friendly.background =
                ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            ll_profetional.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            tv_profetional.setTextColor(resources.getColor(R.color.text_secondary))
            tv_friendly.setTextColor(resources.getColor(R.color.white))
            tv_storytelling.setTextColor(resources.getColor(R.color.text_secondary))

        }
        ll_storytelling.setOnClickListener {
            selectedTone = "story"
            ll_storytelling.background =
                ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            ll_friendly.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_profetional.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            tv_profetional.setTextColor(resources.getColor(R.color.text_secondary))
            tv_friendly.setTextColor(resources.getColor(R.color.text_secondary))
            tv_storytelling.setTextColor(resources.getColor(R.color.white))
        }
        ll_short.setOnClickListener {
            selectedlength = "short"
            ll_long.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_medium.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_short.background =
                ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            tv_short.setTextColor(resources.getColor(R.color.white))
            tv_medium.setTextColor(resources.getColor(R.color.text_secondary))
            tv_long.setTextColor(resources.getColor(R.color.text_secondary))
        }
        ll_medium.setOnClickListener {
            selectedlength = "medium"
            ll_long.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_medium.background =
                ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            ll_short.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            tv_long.setTextColor(resources.getColor(R.color.text_secondary))
            tv_medium.setTextColor(resources.getColor(R.color.white))
            tv_short.setTextColor(resources.getColor(R.color.text_secondary))

        }
        ll_long.setOnClickListener {
            selectedlength = "long"
            ll_long.background =
                ContextCompat.getDrawable(this, R.drawable.chip_selected_background)
            ll_short.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            ll_medium.background =
                ContextCompat.getDrawable(this, R.drawable.chip_unselected_background)
            tv_medium.setTextColor(resources.getColor(R.color.text_secondary))
            tv_short.setTextColor(resources.getColor(R.color.text_secondary))
            tv_long.setTextColor(resources.getColor(R.color.white))
        }

        ll_generate_btn.setOnClickListener {
            generateText()
        }

    }
     fun generateText() {
        // Validate input
        if (input_text_area.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading BEFORE starting the coroutine
        showLoading()

        lifecycleScope.launch {
            val (wordRange, maxTokens) = when (selectedlength) {
                "Short" -> Pair("30-50 words", 150)
                "Medium" -> Pair("50-100 words", 300)
                "Long" -> Pair("100-150 words", 500)
                else -> Pair("100-150 words", 300)
            }

            val prompt = """
Write a LinkedIn post. Do not explain what you're doing, do not use markdown formatting like **bold** or _italics_, and do not add any preamble or commentary. Write only the post content itself as if you are the person posting.

Topic: "${input_text_area.text}"
Tone: "$selectedTone"
Length: "$selectedlength" ($wordRange)
Hashtags: "$includeHashtag"

Guidelines:
- Write in first person as the LinkedIn user
- IMPORTANT: Keep the post EXACTLY within the $wordRange range
- Use natural paragraph breaks (empty lines between paragraphs)
- Keep the tone authentic and conversational
- If hashtags are requested, add 3–5 relevant ones at the end (hashtags don't count toward word limit)
- Use emojis sparingly and only if tone is friendly or motivational
- Start directly with the post content — no "Here's your post:" etc.
- Avoid obvious AI patterns like "In conclusion" or overly structured formatting
- Make every word count — be concise and impactful
            """.trimIndent()

            val result = generator.generateText(
                prompt = prompt
            )

            // Hide loading AFTER getting the result
            hideLoading()

            result.onSuccess { text ->
                Log.d("API responce", text)

                // Navigate to result screen
                val intent = Intent(this@PoastGerenratorInput, ViewGenratedPoastForLinkedIn::class.java)
                intent.putExtra("responce", text)
                intent.putExtra("inputText", input_text_area.text.toString())
                intent.putExtra("selectedTone", selectedTone)
                intent.putExtra("selectedlength", selectedlength)
                intent.putExtra("includeHashtag", includeHashtag)
                startActivity(intent)

            }.onFailure { error ->
                Log.e("HuggingFace", "Error: ${error.message}", error)

                // Show error to user
                Toast.makeText(
                    this@PoastGerenratorInput,
                    "Failed to generate post: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        textStatus.visibility = View.VISIBLE
        textStatus.text = "Crafting your post..."
        ll_generate_btn.isEnabled = false
        ll_generate_btn.alpha = 0.5f
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        textStatus.text = "Successfully Loaded"
        ll_generate_btn.isEnabled = true
        ll_generate_btn.alpha = 1.0f
    }
    private fun iniatView() {
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
        ll_generate_btn = findViewById(R.id.ll_generate_btn)
        textStatus = findViewById(R.id.text_status)
        progressBar = findViewById(R.id.progressBar)
        textStatus.text = "Genrate Post"

    }

    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_dwawer, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_account -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PoastGerenratorInput, MainActivity::class.java)
                    intent.putExtra("from", "profile")
                    startActivity(intent)
                    true
                }

                R.id.menu_settings -> {
                    val intent = Intent(this@PoastGerenratorInput, MainActivity::class.java)
                    intent.putExtra("from", "settings")
                    startActivity(intent)
                    true
                }

                R.id.saved_poast -> {
                    val intent = Intent(this@PoastGerenratorInput, MainActivity::class.java)
                    intent.putExtra("from", "saved")
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


