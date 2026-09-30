package com.foco.app.data

import kotlinx.coroutines.flow.StateFlow

data class SpotifyTrack(
    val title: String,
    val artist: String,
    val isPlaying: Boolean
)

/**
 * Spotify connect + miniplayer surface.
 * Android: Authorization Code + PKCE against Web API.
 * Other platforms: stub until implemented.
 */
interface SpotifyController {
    val connected: StateFlow<Boolean>
    val nowPlaying: StateFlow<SpotifyTrack?>
    /** Short summary when connected (e.g. Spotify display name). */
    val accountSummary: StateFlow<String?>
    /** User-facing status / error for Config screen. */
    val statusMessage: StateFlow<String?>

    fun connect()
    fun disconnect()
    fun playPause()
    fun skip()
    fun pauseAtFocusEnd()

    /**
     * Handle OAuth redirect (App Link / deep link).
     * @return true if the URI was an OAuth callback we consumed.
     */
    fun handleAuthRedirect(uri: String): Boolean
}
