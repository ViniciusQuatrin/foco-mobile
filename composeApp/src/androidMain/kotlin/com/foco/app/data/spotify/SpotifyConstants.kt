package com.foco.app.data.spotify

internal object SpotifyConstants {
    const val REDIRECT_URI = "https://foco-bzo.pages.dev/config"
    const val AUTH_URL = "https://accounts.spotify.com/authorize"
    const val TOKEN_URL = "https://accounts.spotify.com/api/token"
    const val API_BASE = "https://api.spotify.com/v1"
    const val SCOPES = "user-read-currently-playing user-read-playback-state user-modify-playback-state"
    const val PREFS_NAME = "foco_spotify_secure"
}
