package com.omkar.noted.Fragements

import HuggingFaceTextGenerator
import android.widget.EditText
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.omkar.noted.R
import com.omkar.noted.View.ViewGenratedPoastForLinkedIn

class PoastGeneratorInputFragment : Fragment() {

    private lateinit var generator: HuggingFaceTextGenerator

    private lateinit var input_text_area: EditText
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
    private lateinit var ll_generate_btn: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var textStatus: TextView

    private var selectedTone = "professional"
    private var selectedlength = "short"
    private var includeHashtag = "no"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_poast_gerenrator_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        generator = HuggingFaceTextGenerator(requireContext())

        onclickListners()

    }

    private fun initViews(view: View) {
        input_text_area = view.findViewById(R.id.input_text_area)
        ll_profetional = view.findViewById(R.id.ll_professional)
        ll_friendly = view.findViewById(R.id.ll_friendly)
        ll_storytelling = view.findViewById(R.id.ll_story_telling)
        tv_profetional = view.findViewById(R.id.tv_professional)
        tv_friendly = view.findViewById(R.id.tv_friendly)
        tv_storytelling = view.findViewById(R.id.tv_story_telling)
        ll_short = view.findViewById(R.id.ll_short)
        ll_medium = view.findViewById(R.id.ll_medium)
        ll_long = view.findViewById(R.id.ll_long)
        tv_short = view.findViewById(R.id.tv_short)
        tv_medium = view.findViewById(R.id.tv_medium)
        tv_long = view.findViewById(R.id.tv_long)
        ll_generate_btn = view.findViewById(R.id.ll_generate_btn)
        textStatus = view.findViewById(R.id.text_status)
        progressBar = view.findViewById(R.id.progressBar)
    }
    private fun onclickListners() {
        ll_profetional.setOnClickListener {
            selectedTone = "professional"
            ll_storytelling.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            ll_friendly.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            ll_profetional.background =
                ContextCompat.getDrawable(context, R.drawable.chip_selected_background)
            tv_profetional.setTextColor(resources.getColor(R.color.white))
            tv_friendly.setTextColor(resources.getColor(R.color.text_secondary))
            tv_storytelling.setTextColor(resources.getColor(R.color.text_secondary))
        }
        ll_friendly.setOnClickListener {
            selectedTone = "friendly"
            ll_storytelling.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            ll_friendly.background =
                ContextCompat.getDrawable(context, R.drawable.chip_selected_background)
            ll_profetional.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            tv_profetional.setTextColor(resources.getColor(R.color.text_secondary))
            tv_friendly.setTextColor(resources.getColor(R.color.white))
            tv_storytelling.setTextColor(resources.getColor(R.color.text_secondary))

        }
        ll_storytelling.setOnClickListener {
            selectedTone = "story"
            ll_storytelling.background =
                ContextCompat.getDrawable(context, R.drawable.chip_selected_background)
            ll_friendly.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            ll_profetional.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            tv_profetional.setTextColor(resources.getColor(R.color.text_secondary))
            tv_friendly.setTextColor(resources.getColor(R.color.text_secondary))
            tv_storytelling.setTextColor(resources.getColor(R.color.white))
        }
        ll_short.setOnClickListener {
            selectedlength = "short"
            ll_long.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            ll_medium.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            ll_short.background =
                ContextCompat.getDrawable(context, R.drawable.chip_selected_background)
            tv_short.setTextColor(resources.getColor(R.color.white))
            tv_medium.setTextColor(resources.getColor(R.color.text_secondary))
            tv_long.setTextColor(resources.getColor(R.color.text_secondary))
        }
        ll_medium.setOnClickListener {
            selectedlength = "medium"
            ll_long.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            ll_medium.background =
                ContextCompat.getDrawable(context, R.drawable.chip_selected_background)
            ll_short.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            tv_long.setTextColor(resources.getColor(R.color.text_secondary))
            tv_medium.setTextColor(resources.getColor(R.color.white))
            tv_short.setTextColor(resources.getColor(R.color.text_secondary))

        }
        ll_long.setOnClickListener {
            selectedlength = "long"
            ll_long.background =
                ContextCompat.getDrawable(context, R.drawable.chip_selected_background)
            ll_short.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.chip_unselected_background)
            ll_medium.background =
                ContextCompat.getDrawable(context, R.drawable.chip_unselected_background)
            tv_medium.setTextColor(resources.getColor(R.color.text_secondary))
            tv_short.setTextColor(resources.getColor(R.color.text_secondary))
            tv_long.setTextColor(resources.getColor(R.color.white))
        }

        ll_generate_btn.setOnClickListener {
            generateText()
        }

    }

    private fun generateText() {
        if (input_text_area.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please enter a topic", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading()

        lifecycleScope.launchWhenResumed {
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
Hashtags: "yes"

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

            val result = generator.generateText(prompt = prompt)

            hideLoading()

            result.onSuccess { text ->
                val intent = Intent(requireContext(), ViewGenratedPoastForLinkedIn::class.java)
                intent.putExtra("responce", text)
                intent.putExtra("inputText", input_text_area.text.toString())
                intent.putExtra("selectedlength", selectedlength)
                intent.putExtra("selectedTone", selectedTone)
                startActivity(intent)
            }.onFailure { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
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
        textStatus.text = "Generate Post"
        ll_generate_btn.isEnabled = true
        ll_generate_btn.alpha = 1f
    }

//    private fun showPopupMenu(anchor: View) {
//        val popupMenu = PopupMenu(requireContext(), anchor)
//        popupMenu.menuInflater.inflate(R.menu.menu_dwawer, popupMenu.menu)
//
//        popupMenu.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.menu_account -> {
//                    val intent = Intent(requireContext(), MainActivity::class.java)
//                    intent.putExtra("from", "profile")
//                    startActivity(intent)
//                    true
//                }
//                else -> false
//            }
//        }
//        popupMenu.show()
//    }
}
