package com.omkar.noted.View

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.omkar.noted.Genaric.GenricApiCalls
import com.omkar.noted.R

class ViewGenratedPoastForLinkedIn : AppCompatActivity() {
    private var apiresponce=""
    private var includeHashtag=""
    private var selectedlength=""
    private var selectedTone=""
    private var inputText=""
    private lateinit var name:TextView
    private lateinit var position:TextView
    private lateinit var aiPost:EditText
    private lateinit var ll_edit_post:LinearLayout
    private lateinit var ll_copy:LinearLayout
    private lateinit var ll_share:LinearLayout
    private lateinit var ll_save:LinearLayout
    private lateinit var action_back:ImageView
    private lateinit var regenratePost:LinearLayout
    private lateinit var progressBar:ProgressBar
    private lateinit var textStatus:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_genrated_poast_for_linked_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchIntent()
        initView()
        aiPost.setText(apiresponce)
        action_back.setOnClickListener {
            onBackPressed()
        }
        ll_copy.setOnClickListener {
            val textToCopy = aiPost.text.toString()  // get text from EditText
            if (textToCopy.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", textToCopy)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show()
            }
        }

        ll_save.setOnClickListener {
            aiPost.isEnabled = true
        }
        ll_share.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, aiPost.text.toString())
            startActivity(Intent.createChooser(shareIntent, "Share via"))

        }
        ll_edit_post.setOnClickListener {
            aiPost.isEnabled =true
        }
        regenratePost.setOnClickListener {
            showLoading()

            GenricApiCalls(
                userInput = aiPost.text.toString(),
                tone = selectedTone,
                length = selectedlength,
                includeHashtags = includeHashtag
            ) { generatedText ->
                runOnUiThread {  // make sure UI updates happen safely
                    hideLoading()

                    if (generatedText != null) {
                        // ✅ Update your EditText or TextView here
                        aiPost.setText(generatedText)
                    } else {
                        Toast.makeText(this, "Failed to generate post", Toast.LENGTH_SHORT).show()
                    }
                }
            }.generatePost()
        }

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
        name = findViewById(R.id.username)
        position = findViewById(R.id.positon)
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
    }

    private fun fetchIntent() {
        val intent = intent
        if (intent.hasExtra("responce")){
            apiresponce = intent.getStringExtra("responce").toString()
        }
        else if (intent.hasExtra("inputText")){
            inputText =intent.getStringExtra("inputText").toString()
        }
        else if (intent.hasExtra("selectedTone")){
            selectedTone = intent.getStringExtra("selectedTone").toString()
        }
        else if (intent.hasExtra("selectedlength")){
            selectedlength =intent.getStringExtra("includeHashtag").toString()
        }
        else if (intent.hasExtra("includeHashtag")){
            includeHashtag = intent.getStringExtra("includeHashtag").toString()
        }
    }

}