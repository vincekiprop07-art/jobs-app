package com.example.jobsapp.ui.cv

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class CvResult(val rewritten: String, val changes: List<String>)

object CvRewriteClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val ENDPOINT = "https://api.anthropic.com/v1/messages"

    private val systemPrompt = """
        You are a CV/resume editor specializing in making CVs globally readable and ATS (applicant tracking system) friendly.

        Rewrite the CV text the user provides, following these rules:
        - Keep all factual content: employers, titles, dates, schools, skills. Never invent experience, numbers, or qualifications not implied by the original.
        - Convert vague duty descriptions into concise, action-verb-led bullet points.
        - Remove slang, idioms, and culturally specific references that may not translate internationally.
        - Standardize dates to "Mon YYYY" format and use clear, ATS-readable section headers (Experience, Education, Skills, Certifications).
        - Avoid first-person pronouns, decorative symbols, tables, or columns - plain text with simple "- " bullets only.
        - Keep tone professional and neutral, suitable for employers in any country.
        - Preserve the original language of the CV.

        Respond ONLY with valid JSON, no markdown fences, no preamble, in this exact shape:
        {"rewritten": "the full rewritten CV text with \n line breaks", "changes": ["short bullet describing a change", "..."]}
        Keep changes to 3-6 concise bullets, each under 15 words.
    """.trimIndent()

    @Throws(IOException::class)
    fun rewrite(apiKey: String, cvText: String): CvResult {
        val messages = JSONArray().put(
            JSONObject().put("role", "user").put("content", cvText)
        )
        val payload = JSONObject()
            .put("model", "claude-sonnet-4-6")
            .put("max_tokens", 4000)
            .put("system", systemPrompt)
            .put("messages", messages)

        val body = payload.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(ENDPOINT)
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("content-type", "application/json")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IOException("Empty response")
            if (!response.isSuccessful) {
                throw IOException("API error (${response.code}): $responseBody")
            }

            val json = JSONObject(responseBody)
            val content = json.getJSONArray("content")
            var textBlock: String? = null
            for (i in 0 until content.length()) {
                val block = content.getJSONObject(i)
                if (block.optString("type") == "text") {
                    textBlock = block.getString("text")
                    break
                }
            }
            val raw = textBlock ?: throw IOException("No text returned from the model")
            val cleaned = raw.trim()
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```").trim()

            return try {
                val parsed = JSONObject(cleaned)
                val changes = mutableListOf<String>()
                if (parsed.has("changes")) {
                    val arr = parsed.getJSONArray("changes")
                    for (i in 0 until arr.length()) changes.add(arr.getString(i))
                }
                CvResult(parsed.optString("rewritten", cleaned), changes)
            } catch (e: Exception) {
                CvResult(cleaned, emptyList())
            }
        }
    }
}
