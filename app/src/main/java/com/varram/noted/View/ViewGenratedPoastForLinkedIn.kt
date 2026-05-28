package com.varram.noted.View

import HuggingFaceTextGenerator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.varram.noted.Database.DatabaseHelper
import com.varram.noted.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewGenratedPoastForLinkedIn : AppCompatActivity() {
    private var apiresponce=""
    private var includeHashtag=""
    private var selectedlength=""
    private var selectedTone=""
    private var genratedOn=""
    private var from=""
    private var postTitle=""
    private var inputText=""
    private lateinit var username:TextView
    private lateinit var userImage:ImageView
    private lateinit var position:TextView
    private lateinit var aiPost:EditText
    private lateinit var ll_edit_post:LinearLayout
    private lateinit var ll_copy:LinearLayout
    private lateinit var ll_share:LinearLayout
    private lateinit var ll_save:LinearLayout
    private lateinit var action_back:ImageView
    private lateinit var menu_icon:ImageView
    private lateinit var regenratePost:LinearLayout
    private lateinit var progressBar:ProgressBar
    private lateinit var textStatus:TextView
    private lateinit var db:DatabaseHelper
    private lateinit var generator : HuggingFaceTextGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_genrated_poast_for_linked_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = DatabaseHelper(this@ViewGenratedPoastForLinkedIn)
        generator= HuggingFaceTextGenerator(this@ViewGenratedPoastForLinkedIn)
        fetchIntent()
        initView()
        fetchNameAndImageFromDB()
        aiPost.setText(apiresponce)
        action_back.setOnClickListener {
            onBackPressed()
        }


        ll_copy.setOnClickListener {
            val textToCopy = aiPost.text.toString()  // get text from EditText
            if (textToCopy.isNotEmpty()) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", textToCopy)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show()
            }
        }
        ll_share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, aiPost.text.toString())
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
        menu_icon.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, aiPost.text.toString())
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
        ll_edit_post.setOnClickListener {
            aiPost.isEnabled =true
        }
        ll_save.setOnClickListener {
            if (from.equals("savedpost")){
                Toast.makeText(this@ViewGenratedPoastForLinkedIn, "This Post is already saved",Toast.LENGTH_SHORT).show()
            }
            else{
                val currentTime = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                try {

                    if (aiPost.text.equals("") || inputText.equals("")){
                        Toast.makeText(this, "Post cannot be saved", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val inserted = db.insertSavedPost(currentTime, aiPost.text.toString(),inputText)

                        if (inserted != -1L) {
                            Toast.makeText(this, "Post Saved Successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to Save Post", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: Exception) {
                    Log.e("ERROR", e.toString())
                }
            }
        }
        regenratePost.setOnClickListener {
            generateText()
        }

    }
    fun generateText() {
        // Validate input
        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading BEFORE starting the coroutine
        showLoading()

        lifecycleScope.launch {
            val prompt = """
Write a LinkedIn post. Do not explain what you're doing, do not use markdown formatting like **bold** or _italics_, and do not add any preamble or commentary. Write only the post content itself as if you are the person posting.

Topic: "${inputText}"
Tone: "$selectedTone"
Length: "$selectedlength"
Hashtags: "$includeHashtag"

Guidelines:
- Write in first person as the LinkedIn user
- Use natural paragraph breaks (empty lines between paragraphs)
- Keep the tone authentic and conversational
- If hashtags are requested, add 3–5 relevant ones at the end
- Use emojis sparingly and only if tone is friendly or motivational
- Start directly with the post content — no "Here's your post:" etc.
- Avoid obvious AI patterns like "In conclusion" or overly structured formatting
            """.trimIndent()
            Log.d("Prompt",prompt)
            val result = generator.generateText(
                prompt = prompt
            )

            // Hide loading AFTER getting the result
            hideLoading()

            result.onSuccess { text ->
                Log.d("API responce", text)
                aiPost.setText(text)

            }.onFailure { error ->
                Log.e("HuggingFace", "Error: ${error.message}", error)

                // Show error to user
                Toast.makeText(
                    this@ViewGenratedPoastForLinkedIn,
                    "Failed to generate post: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun fetchNameAndImageFromDB() {
        val (name, imageUrl) = db.getImageAndName()
        username.text = name.toString()
        Glide.with(this@ViewGenratedPoastForLinkedIn)
            .asBitmap()
            .load(imageUrl)
            .placeholder(R.drawable.profilepicture)
            .into(userImage)
    }

    fun showLoading() {
        progressBar.visibility = View.VISIBLE
        textStatus.text = "Crafting your post"
        regenratePost.alpha = 0.5f
    }
    fun hideLoading() {
        progressBar.visibility = View.GONE
        textStatus.text = "Loaded successfully!"
        regenratePost.alpha = 1.0f
    }

    private fun initView() {
        username = findViewById(R.id.username)
        userImage = findViewById(R.id.userImage)
        aiPost = findViewById(R.id.tv_genrated)
        aiPost.isEnabled = false
        action_back = findViewById(R.id.action_back)
        ll_edit_post = findViewById(R.id.ll_edit_post)
        ll_copy = findViewById(R.id.ll_copy)
        ll_share = findViewById(R.id.ll_share)
        ll_save = findViewById(R.id.ll_savePost)
        regenratePost = findViewById(R.id.regenratePost)
        textStatus = findViewById(R.id.text_status)
        progressBar = findViewById(R.id.progressBar)
        menu_icon = findViewById(R.id.menu_icon)


        if (from.equals("savedpost")){
            executeFromeSavedPostLogic()
        }
    }

    private fun executeFromeSavedPostLogic() {
        regenratePost.visibility =View.GONE


    }

    private fun fetchIntent() {
        val intent = intent
        if (intent.hasExtra("responce")){
            apiresponce = intent.getStringExtra("responce").toString()
        }
        if (intent.hasExtra("from")){
            from = intent.getStringExtra("from").toString()
            if (intent.hasExtra("posttext")){
                apiresponce = intent.getStringExtra("posttext").toString()
            }
        }
        if (intent.hasExtra("inputText")){
            inputText =intent.getStringExtra("inputText").toString()
        }

        if (intent.hasExtra("selectedTone")){
            selectedTone = intent.getStringExtra("selectedTone").toString()
        }
        if (intent.hasExtra("selectedlength")){
            selectedlength =intent.getStringExtra("selectedlength").toString()
        }
        if (intent.hasExtra("includeHashtag")){
            includeHashtag = intent.getStringExtra("includeHashtag").toString()
        }
    }

}