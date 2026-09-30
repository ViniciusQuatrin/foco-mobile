package com.foco.app.data.spotify

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.foco.app.data.SpotifyController
import com.foco.app.data.SpotifyTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class AndroidSpotifyController(
    context: Context,
    private val clientId: String
) : SpotifyController {

    private val appContext = context.applicationContext
    private val store = SpotifyTokenStore(appContext)
    private val api = SpotifyApiClient(clientId, store)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _connected = MutableStateFlow(store.hasTokens())
    override val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _nowPlaying = MutableStateFlow<SpotifyTrack?>(null)
    override val nowPlaying: StateFlow<SpotifyTrack?> = _nowPlaying.asStateFlow()

    private val _accountSummary = MutableStateFlow(store.displayName)
    override val accountSummary: StateFlow<String?> = _accountSummary.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    override val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var pollJob: Job? = null

    init {
        if (store.hasTokens()) {
            _statusMessage.value = "Conectado"
            scope.launch { bootstrapSession() }
        }
    }

    override fun connect() {
        if (clientId.isBlank()) {
            _statusMessage.value = "SPOTIFY_CLIENT_ID ausente no build."
            return
        }
        val verifier = Pkce.generateCodeVerifier()
        val state = Pkce.generateState()
        store.pendingVerifier = verifier
        store.pendingState = state
        val challenge = Pkce.codeChallengeS256(verifier)
        val url = buildString {
            append(SpotifyConstants.AUTH_URL)
            append("?client_id=").append(enc(clientId))
            append("&response_type=code")
            append("&redirect_uri=").append(enc(SpotifyConstants.REDIRECT_URI))
            append("&code_challenge_method=S256")
            append("&code_challenge=").append(enc(challenge))
            append("&scope=").append(enc(SpotifyConstants.SCOPES))
            append("&state=").append(enc(state))
            append("&show_dialog=false")
        }
        _statusMessage.value = "Abrindo Spotify…"
        val customTabs = CustomTabsIntent.Builder().setShowTitle(true).build()
        customTabs.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            customTabs.launchUrl(appContext, Uri.parse(url))
        } catch (_: Exception) {
            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(fallback)
        }
    }

    override fun disconnect() {
        stopPolling()
        store.clearAll()
        _connected.value = false
        _nowPlaying.value = null
        _accountSummary.value = null
        _statusMessage.value = "Desconectado"
    }

    override fun playPause() {
        scope.launch {
            val playing = _nowPlaying.value?.isPlaying == true
            val result = withContext(Dispatchers.IO) {
                if (playing) api.pause() else api.play()
            }
            result.onFailure {
                _statusMessage.value = it.message ?: "Falha no playback"
            }
            refreshNowPlaying()
        }
    }

    override fun skip() {
        scope.launch {
            val result = withContext(Dispatchers.IO) { api.next() }
            result.onFailure {
                _statusMessage.value = it.message ?: "Falha ao pular"
            }
            delay(400)
            refreshNowPlaying()
        }
    }

    override fun pauseAtFocusEnd() {
        scope.launch {
            withContext(Dispatchers.IO) { api.pause() }
            refreshNowPlaying()
        }
    }

    override fun handleAuthRedirect(uri: String): Boolean {
        val parsed = try {
            Uri.parse(uri)
        } catch (_: Exception) {
            return false
        }
        if (!isOAuthRedirect(parsed)) return false

        val error = parsed.getQueryParameter("error")
        if (!error.isNullOrBlank()) {
            store.clearPendingAuth()
            _statusMessage.value = "OAuth negado: $error"
            return true
        }

        val code = parsed.getQueryParameter("code") ?: return false
        val state = parsed.getQueryParameter("state")
        val expectedState = store.pendingState
        if (expectedState != null && state != expectedState) {
            store.clearPendingAuth()
            _statusMessage.value = "State OAuth inválido."
            return true
        }
        val verifier = store.pendingVerifier
        if (verifier.isNullOrBlank()) {
            _statusMessage.value = "PKCE verifier ausente — toque Conectar de novo."
            return true
        }

        _statusMessage.value = "Trocando código…"
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                api.exchangeCode(code, verifier)
            }
            store.clearPendingAuth()
            result.fold(
                onSuccess = {
                    _connected.value = true
                    _statusMessage.value = "Conectado"
                    bootstrapSession()
                },
                onFailure = {
                    _connected.value = false
                    _statusMessage.value = it.message ?: "Falha no token"
                }
            )
        }
        return true
    }

    private suspend fun bootstrapSession() {
        val name = withContext(Dispatchers.IO) {
            runCatching { api.fetchMe() }.getOrNull()
        }
        if (name != null) {
            _accountSummary.value = name
            _connected.value = true
        } else if (!store.hasTokens()) {
            _connected.value = false
            return
        } else {
            _accountSummary.value = store.displayName ?: "Spotify"
            _connected.value = true
        }
        startPolling()
        refreshNowPlaying()
    }

    private fun startPolling() {
        if (pollJob?.isActive == true) return
        pollJob = scope.launch {
            while (isActive && _connected.value) {
                refreshNowPlaying()
                delay(5_000)
            }
        }
    }

    private fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
    }

    private suspend fun refreshNowPlaying() {
        if (!_connected.value && !store.hasTokens()) return
        val track = withContext(Dispatchers.IO) {
            runCatching { api.currentlyPlaying() }.getOrElse {
                if (it.message?.contains("Refresh failed") == true ||
                    it.message?.contains("Not connected") == true
                ) {
                    _connected.value = false
                    _statusMessage.value = "Sessão Spotify expirada"
                    stopPolling()
                }
                null
            }
        }
        _nowPlaying.value = track
    }

    private fun isOAuthRedirect(uri: Uri): Boolean {
        if (uri.scheme != "https") return false
        if (uri.host != "foco-bzo.pages.dev") return false
        val path = uri.path?.trimEnd('/') ?: ""
        if (path != "/config") return false
        return !uri.getQueryParameter("code").isNullOrBlank() ||
            !uri.getQueryParameter("error").isNullOrBlank()
    }

    private fun enc(s: String): String =
        URLEncoder.encode(s, StandardCharsets.UTF_8.name())
}
