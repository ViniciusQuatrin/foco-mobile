package com.foco.app.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foco.app.data.SettingsRepository
import com.foco.app.data.SpotifyController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConfigViewModel(
    private val settingsRepo: SettingsRepository,
    private val spotify: SpotifyController
) : ViewModel() {
    val config: StateFlow<AppConfig> = settingsRepo.config
    val spotifyConnected = spotify.connected
    val spotifySummary = spotify.accountSummary
    val spotifyStatus = spotify.statusMessage

    init {
        viewModelScope.launch {
            spotify.connected.collectLatest { on ->
                if (settingsRepo.config.value.spotifyConnected != on) {
                    settingsRepo.update { it.copy(spotifyConnected = on) }
                }
            }
        }
    }

    fun setDurationUnit(unit: DurationUnit) {
        settingsRepo.update { it.copy(durationUnit = unit) }
    }

    fun setFocusDurationFromDisplay(value: Int) {
        if (value <= 0) return
        val secs = (value * settingsRepo.config.value.durationUnit.secondsFactor).coerceAtLeast(1)
        settingsRepo.update { it.copy(focusDurationSeconds = secs) }
    }

    fun setBreakDurationFromDisplay(value: Int) {
        if (value <= 0) return
        val secs = (value * settingsRepo.config.value.durationUnit.secondsFactor).coerceAtLeast(1)
        settingsRepo.update { it.copy(breakDurationSeconds = secs) }
    }

    fun setLongBreakDurationFromDisplay(value: Int) {
        if (value <= 0) return
        val secs = (value * settingsRepo.config.value.durationUnit.secondsFactor).coerceAtLeast(1)
        settingsRepo.update { it.copy(longBreakDurationSeconds = secs) }
    }

    fun setSound(enabled: Boolean) = settingsRepo.update { it.copy(soundEnabled = enabled) }
    fun setNotifications(enabled: Boolean) = settingsRepo.update { it.copy(notificationsEnabled = enabled) }
    fun setTheme(mode: ThemeMode) = settingsRepo.update { it.copy(themeMode = mode) }
    fun setDisplayName(name: String) = settingsRepo.update { it.copy(displayName = name) }
    fun setDefaultSessionName(name: String) = settingsRepo.update { it.copy(defaultSessionName = name) }
    fun setPauseSpotifyOnEnd(enabled: Boolean) =
        settingsRepo.update { it.copy(pauseSpotifyOnFocusEnd = enabled) }

    fun connectSpotify() {
        spotify.connect()
    }

    fun disconnectSpotify() {
        spotify.disconnect()
        settingsRepo.update { it.copy(spotifyConnected = false) }
    }

    fun displayValue(seconds: Int): Int {
        val factor = settingsRepo.config.value.durationUnit.secondsFactor
        return (seconds / factor).coerceAtLeast(0)
    }
}
