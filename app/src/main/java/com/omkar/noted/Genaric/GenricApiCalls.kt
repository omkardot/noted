package com.omkar.noted.Genaric

import android.content.Intent
import android.util.Log
import com.omkar.noted.View.ViewGenratedPoastForLinkedIn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GenricApiCalls(
    private val userInput: String,
    private val tone: String,
    private val length: String,
    private val includeHashtags: String,
    private val callback: (String?) -> Unit // callback function
) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(GeminiApiService::class.java)

    fun generatePost() {
        val prompt = """
            Write a LinkedIn post. Do not explain what you're doing, do not use markdown formatting like **bold** or _italics_, and do not add any preamble or commentary. Write only the post content itself as if you are the person posting.

Topic: "$userInput"
Tone: "$tone"
Length: "$length"
Hashtags: "$includeHashtags"

Guidelines:
- Write in first person as the LinkedIn user
- Use natural paragraph breaks (empty lines between paragraphs)
- Keep the tone authentic and conversational
- If hashtags are requested, add 3–5 relevant ones at the end
- Use emojis sparingly and only if tone is friendly or motivational
- Start directly with the post content — no “Here's your post:” etc.
- Avoid obvious AI patterns like “In conclusion” or overly structured formatting
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(prompt)))
            )
        )

        val apiKey = "AIzaSyDguLqqJjP4t1gj0_A9CFujd-qsyOI0oG0"

        service.generateText(apiKey, request).enqueue(object : Callback<GeminiResponse> {
            override fun onResponse(
                call: Call<GeminiResponse>,
                response: Response<GeminiResponse>
            ) {
                if (response.isSuccessful) {
                    val text = response.body()
                        ?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()
                        ?.text

                    val cleanText = text
                        ?.replace(Regex("(?i)^(okay|sure|here('|’)s|alright|let me).*?:?\\s*", RegexOption.MULTILINE), "")
                        ?.trim()

                    callback(cleanText) // ✅ return text to UI
                } else {
                    Log.e("Gemini", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                Log.e("Gemini", "Failure: ${t.message}")
                callback(null)
            }
        })
    }
}
