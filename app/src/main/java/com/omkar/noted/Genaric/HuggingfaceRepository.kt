import android.content.Context
import android.util.Log
import com.omkar.noted.R
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class HuggingFaceTextGenerator(context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(90, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val apiToken = context.resources.getString(R.string.huggingfacetoken)
    private val TAG = "HF_ROUTER"

    suspend fun generateText(prompt: String): Result<String> = withContext(Dispatchers.IO) {

        if (apiToken.isBlank()) {
            return@withContext Result.failure(Exception("Missing HuggingFace Token"))
        }

        try {
            // --- NEW REQUEST FORMAT (Chat Completions) ---
            val messageArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are a helpful assistant.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }

            val jsonBody = JSONObject().apply {
                put("model", "meta-llama/Llama-3.2-1B-Instruct")
                put("max_tokens", 300)
                put("messages", messageArray)
            }

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaType())


            // --- NEW ENDPOINT ---
            val url = "https://router.huggingface.co/v1/chat/completions"

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiToken")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            Log.d(TAG, "Sending request → $url")

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            Log.d(TAG, "Response Code: ${response.code}")
            Log.d(TAG, "Response Body: $responseBody")

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("API Error ${response.code}: $responseBody"))
            }

            // --- Parse OpenAI-style response ---
            val parsed = JSONObject(responseBody)
            val content = parsed
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            return@withContext Result.success(content.trim())

        } catch (e: Exception) {
            Log.e(TAG, "Error", e)
            return@withContext Result.failure(e)
        }
    }
}
