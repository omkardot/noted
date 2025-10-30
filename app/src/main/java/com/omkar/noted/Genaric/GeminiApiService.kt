package com.omkar.noted.Genaric

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    fun generateText(
        @Query("key") apiKey: String,
        @Body body: GeminiRequest
    ): Call<GeminiResponse>
}

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: ContentText?
)

data class ContentText(
    val parts: List<PartText>?
)

data class PartText(
    val text: String?
)
