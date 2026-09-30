package com.foco.app.data.spotify

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal data class HttpResult(
    val code: Int,
    val body: String
) {
    val ok: Boolean get() = code in 200..299
    fun jsonOrNull(): JSONObject? = try {
        if (body.isBlank()) null else JSONObject(body)
    } catch (_: Exception) {
        null
    }
}

internal object SpotifyHttp {
    fun postForm(url: String, form: Map<String, String>, headers: Map<String, String> = emptyMap()): HttpResult {
        val encoded = form.entries.joinToString("&") { (k, v) ->
            "${enc(k)}=${enc(v)}"
        }
        return request("POST", url, encoded, headers + ("Content-Type" to "application/x-www-form-urlencoded"))
    }

    fun get(url: String, bearer: String): HttpResult =
        request("GET", url, null, mapOf("Authorization" to "Bearer $bearer"))

    fun put(url: String, bearer: String, body: String? = null): HttpResult =
        request(
            "PUT",
            url,
            body,
            mapOf(
                "Authorization" to "Bearer $bearer",
                "Content-Type" to "application/json"
            )
        )

    fun post(url: String, bearer: String): HttpResult =
        request(
            "POST",
            url,
            null,
            mapOf("Authorization" to "Bearer $bearer")
        )

    private fun request(
        method: String,
        url: String,
        body: String?,
        headers: Map<String, String>
    ): HttpResult {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 15_000
            doInput = true
            instanceFollowRedirects = true
            headers.forEach { (k, v) -> setRequestProperty(k, v) }
            if (body != null) {
                doOutput = true
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { it.write(body) }
            }
        }
        return try {
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.let { s ->
                BufferedReader(InputStreamReader(s, StandardCharsets.UTF_8)).use { it.readText() }
            }.orEmpty()
            HttpResult(code, text)
        } finally {
            conn.disconnect()
        }
    }

    private fun enc(s: String): String =
        URLEncoder.encode(s, StandardCharsets.UTF_8.name())
}
