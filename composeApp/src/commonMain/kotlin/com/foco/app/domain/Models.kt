package com.foco.app.domain

import com.foco.app.ui.Strings

enum class DurationUnit(val label: String, val secondsFactor: Int) {
    SECONDS(Strings.UNIT_S, 1),
    MINUTES(Strings.UNIT_MIN, 60),
    HOURS(Strings.UNIT_H, 3600);

    companion object {
        fun fromKey(key: String): DurationUnit =
            entries.find { it.name.equals(key, ignoreCase = true) } ?: MINUTES
    }
}

enum class TimerMode(val label: String, val shortLabel: String) {
    FOCUS(Strings.MODE_FOCUS, Strings.MODE_FOCUS),
    BREAK_SHORT(Strings.MODE_BREAK_SHORT, Strings.MODE_SHORT),
    BREAK_LONG(Strings.MODE_BREAK_LONG, Strings.MODE_LONG);

    /** Legacy alias used by older call sites that still say BREAK. */
    companion object {
        val BREAK get() = BREAK_SHORT
    }
}

enum class ThemeMode(val label: String) {
    DARK_CYBER(Strings.THEME_DARK),
    LIGHT(Strings.THEME_LIGHT),
    SYSTEM("Sistema");

    companion object {
        fun fromKey(key: String): ThemeMode =
            entries.find { it.name.equals(key, ignoreCase = true) } ?: DARK_CYBER
    }
}

data class FocusSession(
    val id: String,
    val name: String,
    val mode: TimerMode,
    val plannedSeconds: Int,
    val completedSeconds: Int,
    val finishedAtEpochMs: Long,
    val completed: Boolean
)

data class AppConfig(
    val durationUnit: DurationUnit = DurationUnit.MINUTES,
    val focusDurationSeconds: Int = 25 * 60,
    val breakDurationSeconds: Int = 5 * 60,
    val longBreakDurationSeconds: Int = 15 * 60,
    val soundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.DARK_CYBER,
    val displayName: String = "",
    val defaultSessionName: String = "",
    val spotifyConnected: Boolean = false,
    val pauseSpotifyOnFocusEnd: Boolean = true
)

enum class TimerStatus {
    IDLE, RUNNING, PAUSED, FINISHED
}

data class TimerState(
    val mode: TimerMode = TimerMode.FOCUS,
    val status: TimerStatus = TimerStatus.IDLE,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val sessionName: String = "",
    val endFeedback: String = "",
    val tick: Long = 0L
) {
    val progress: Float
        get() = if (totalSeconds <= 0) 0f else 1f - (remainingSeconds.toFloat() / totalSeconds)

    val statusLabel: String
        get() = when (status) {
            TimerStatus.IDLE, TimerStatus.FINISHED -> Strings.STATUS_IDLE
            TimerStatus.RUNNING -> Strings.STATUS_RUNNING
            TimerStatus.PAUSED -> Strings.STATUS_PAUSED
        }
}
