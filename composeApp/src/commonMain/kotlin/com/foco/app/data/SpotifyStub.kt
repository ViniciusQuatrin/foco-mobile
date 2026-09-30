package com.foco.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Stub for non-Android targets / missing Client ID. */
class SpotifyStubController : SpotifyController {
    private val _connected = MutableStateFlow(false)
    override val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _nowPlaying = MutableStateFlow<SpotifyTrack?>(null)
    override val nowPlaying: StateFlow<SpotifyTrack?> = _nowPlaying.asStateFlow()

    private val _accountSummary = MutableStateFlow<String?>(null)
    override val accountSummary: StateFlow<String?> = _accountSummary.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    override val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    override fun connect() {
        _connected.value = true
        _accountSummary.value = "Demo (stub)"
        _statusMessage.value = "Modo stub — OAuth real só no Android com Client ID."
        _nowPlaying.value = SpotifyTrack(
            title = "Neon Pulse (demo)",
            artist = "FOCO Stub",
            isPlaying = true
        )
    }

    override fun disconnect() {
        _connected.value = false
        _accountSummary.value = null
        _statusMessage.value = null
        _nowPlaying.value = null
    }

    override fun playPause() {
        val cur = _nowPlaying.value ?: return
        _nowPlaying.value = cur.copy(isPlaying = !cur.isPlaying)
    }

    override fun skip() {
        val cur = _nowPlaying.value ?: return
        _nowPlaying.value = cur.copy(
            title = "Próxima faixa (stub)",
            artist = cur.artist,
            isPlaying = true
        )
    }

    override fun pauseAtFocusEnd() {
        val cur = _nowPlaying.value ?: return
        if (cur.isPlaying) {
            _nowPlaying.value = cur.copy(isPlaying = false)
        }
    }

    override fun handleAuthRedirect(uri: String): Boolean = false
}
