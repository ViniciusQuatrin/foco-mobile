package com.foco.app.data.spotify

import com.foco.app.data.SpotifyTrack
import org.json.JSONObject

internal class SpotifyApiClient(
    private val clientId: String,
    private val store: SpotifyTokenStore
) {
    suspend fun exchangeCode(code: String, codeVerifier: String): Result<Unit> = runCatching {
        val res = SpotifyHttp.postForm(
            SpotifyConstants.TOKEN_URL,
            mapOf(
                "grant_type" to "authorization_code",
                "code" to code,
                "redirect_uri" to SpotifyConstants.REDIRECT_URI,
                "client_id" to clientId,
                "code_verifier" to codeVerifier
            )
        )
        if (!res.ok) error("Token exchange failed (${res.code})")
        persistTokens(res.jsonOrNull() ?: error("Empty token response"))
    }

    suspend fun refreshIfNeeded(): String {
        val token = store.accessToken ?: error("Not connected")
        val skewMs = 60_000L
        if (System.currentTimeMillis() + skewMs < store.expiresAtEpochMs) {
            return token
        }
        val refresh = store.refreshToken ?: error("No refresh token")
        val res = SpotifyHttp.postForm(
            SpotifyConstants.TOKEN_URL,
            mapOf(
                "grant_type" to "refresh_token",
                "refresh_token" to refresh,
                "client_id" to clientId
            )
        )
        if (!res.ok) {
            store.clearTokens()
            error("Refresh failed (${res.code})")
        }
        persistTokens(res.jsonOrNull() ?: error("Empty refresh response"), keepRefresh = refresh)
        return store.accessToken ?: error("Missing access token after refresh")
    }

    suspend fun fetchMe(): String? {
        val bearer = refreshIfNeeded()
        val res = SpotifyHttp.get("${SpotifyConstants.API_BASE}/me", bearer)
        if (!res.ok) return null
        val json = res.jsonOrNull() ?: return null
        val name = json.optString("display_name").ifBlank {
            json.optString("id").ifBlank { "Spotify" }
        }
        store.displayName = name
        return name
    }

    suspend fun currentlyPlaying(): SpotifyTrack? {
        val bearer = refreshIfNeeded()
        val res = SpotifyHttp.get("${SpotifyConstants.API_BASE}/me/player/currently-playing", bearer)
        if (res.code == 204 || res.body.isBlank()) return null
        if (!res.ok) return null
        val json = res.jsonOrNull() ?: return null
        val item = json.optJSONObject("item") ?: return null
        val title = item.optString("name").ifBlank { "Unknown" }
        val artists = item.optJSONArray("artists")
        val artist = buildString {
            if (artists != null) {
                for (i in 0 until artists.length()) {
                    val a = artists.optJSONObject(i)?.optString("name").orEmpty()
                    if (a.isNotBlank()) {
                        if (isNotEmpty()) append(", ")
                        append(a)
                    }
                }
            }
        }.ifBlank { "Spotify" }
        val isPlaying = json.optBoolean("is_playing", false)
        return SpotifyTrack(title = title, artist = artist, isPlaying = isPlaying)
    }

    suspend fun play(): Result<Unit> = playbackAction("play")
    suspend fun pause(): Result<Unit> = playbackAction("pause")
    suspend fun next(): Result<Unit> = runCatching {
        val bearer = refreshIfNeeded()
        val res = SpotifyHttp.post("${SpotifyConstants.API_BASE}/me/player/next", bearer)
        // 204 success; 404 no active device
        if (res.code !in listOf(200, 204) && res.code != 404) {
            error("Skip failed (${res.code})")
        }
    }

    private suspend fun playbackAction(action: String): Result<Unit> = runCatching {
        val bearer = refreshIfNeeded()
        val res = SpotifyHttp.put("${SpotifyConstants.API_BASE}/me/player/$action", bearer)
        if (res.code !in listOf(200, 204) && res.code != 404) {
            error("$action failed (${res.code})")
        }
    }

    private fun persistTokens(json: JSONObject, keepRefresh: String? = null) {
        store.accessToken = json.getString("access_token")
        val newRefresh = json.optString("refresh_token").takeIf { it.isNotBlank() }
        store.refreshToken = newRefresh ?: keepRefresh ?: store.refreshToken
        val expiresIn = json.optLong("expires_in", 3600L)
        store.expiresAtEpochMs = System.currentTimeMillis() + expiresIn * 1000L
    }
}
