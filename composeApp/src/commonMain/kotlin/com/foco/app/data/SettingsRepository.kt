package com.foco.app.data

import com.foco.app.domain.AppConfig
import com.foco.app.domain.DurationUnit
import com.foco.app.domain.ThemeMode
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(
    private val settings: Settings
) {
    private val _config = MutableStateFlow(load())
    val config: StateFlow<AppConfig> = _config.asStateFlow()

    private fun load(): AppConfig = AppConfig(
        durationUnit = DurationUnit.fromKey(settings.getString(KEY_UNIT, DurationUnit.MINUTES.name)),
        focusDurationSeconds = settings.getInt(KEY_FOCUS_SEC, 25 * 60),
        breakDurationSeconds = settings.getInt(KEY_BREAK_SEC, 5 * 60),
        longBreakDurationSeconds = settings.getInt(KEY_LONG_BREAK_SEC, 15 * 60),
        soundEnabled = settings.getBoolean(KEY_SOUND, true),
        notificationsEnabled = settings.getBoolean(KEY_NOTIF, true),
        themeMode = ThemeMode.fromKey(settings.getString(KEY_THEME, ThemeMode.DARK_CYBER.name)),
        displayName = settings.getString(KEY_NAME, ""),
        defaultSessionName = settings.getString(KEY_DEFAULT_SESSION, ""),
        spotifyConnected = settings.getBoolean(KEY_SPOTIFY, false),
        pauseSpotifyOnFocusEnd = settings.getBoolean(KEY_PAUSE_SPOTIFY, true)
    )

    fun update(transform: (AppConfig) -> AppConfig) {
        val next = transform(_config.value)
        persist(next)
        _config.value = next
    }

    private fun persist(c: AppConfig) {
        settings.putString(KEY_UNIT, c.durationUnit.name)
        settings.putInt(KEY_FOCUS_SEC, c.focusDurationSeconds)
        settings.putInt(KEY_BREAK_SEC, c.breakDurationSeconds)
        settings.putInt(KEY_LONG_BREAK_SEC, c.longBreakDurationSeconds)
        settings.putBoolean(KEY_SOUND, c.soundEnabled)
        settings.putBoolean(KEY_NOTIF, c.notificationsEnabled)
        settings.putString(KEY_THEME, c.themeMode.name)
        settings.putString(KEY_NAME, c.displayName)
        settings.putString(KEY_DEFAULT_SESSION, c.defaultSessionName)
        settings.putBoolean(KEY_SPOTIFY, c.spotifyConnected)
        settings.putBoolean(KEY_PAUSE_SPOTIFY, c.pauseSpotifyOnFocusEnd)
    }

    companion object {
        private const val KEY_UNIT = "duration_unit"
        private const val KEY_FOCUS_SEC = "focus_sec"
        private const val KEY_BREAK_SEC = "break_sec"
        private const val KEY_LONG_BREAK_SEC = "long_break_sec"
        private const val KEY_SOUND = "sound"
        private const val KEY_NOTIF = "notif"
        private const val KEY_THEME = "theme"
        private const val KEY_NAME = "display_name"
        private const val KEY_DEFAULT_SESSION = "default_session"
        private const val KEY_SPOTIFY = "spotify_connected"
        private const val KEY_PAUSE_SPOTIFY = "pause_spotify_on_end"
    }
}
