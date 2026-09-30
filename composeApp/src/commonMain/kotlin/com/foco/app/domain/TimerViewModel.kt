package com.foco.app.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foco.app.data.SessionHistoryRepository
import com.foco.app.data.SettingsRepository
import com.foco.app.data.SpotifyController
import com.foco.app.ui.Strings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class TimerViewModel(
    private val settingsRepo: SettingsRepository,
    private val historyRepo: SessionHistoryRepository,
    private val spotify: SpotifyController
) : ViewModel() {

    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    val config = settingsRepo.config
    val spotifyConnected = spotify.connected
    val nowPlaying = spotify.nowPlaying

    private var ticker: Job? = null

    private fun durationFor(mode: TimerMode, cfg: AppConfig = settingsRepo.config.value): Int =
        when (mode) {
            TimerMode.FOCUS -> cfg.focusDurationSeconds
            TimerMode.BREAK_SHORT -> cfg.breakDurationSeconds
            TimerMode.BREAK_LONG -> cfg.longBreakDurationSeconds
        }

    private fun initialState(): TimerState {
        val cfg = settingsRepo.config.value
        val total = cfg.focusDurationSeconds
        return TimerState(
            mode = TimerMode.FOCUS,
            remainingSeconds = total,
            totalSeconds = total,
            sessionName = cfg.defaultSessionName
        )
    }

    fun setMode(mode: TimerMode) {
        if (_state.value.status == TimerStatus.RUNNING) return
        val total = durationFor(mode)
        stopTicker()
        _state.value = TimerState(
            mode = mode,
            status = TimerStatus.IDLE,
            remainingSeconds = total,
            totalSeconds = total,
            sessionName = _state.value.sessionName
        )
    }

    /** Web parity: tap mode tab cycles FOCO → PAUSA CURTA → PAUSA LONGA. */
    fun cycleMode() {
        if (_state.value.status == TimerStatus.RUNNING) return
        val order = listOf(TimerMode.FOCUS, TimerMode.BREAK_SHORT, TimerMode.BREAK_LONG)
        val idx = order.indexOf(_state.value.mode).coerceAtLeast(0)
        setMode(order[(idx + 1) % order.size])
    }

    fun setSessionName(name: String) {
        _state.update { it.copy(sessionName = name) }
    }

    fun playPause() {
        when (_state.value.status) {
            TimerStatus.RUNNING -> pause()
            TimerStatus.FINISHED -> restartAndPlay()
            else -> play()
        }
    }

    fun reset() {
        stopTicker()
        val total = durationFor(_state.value.mode)
        _state.update {
            it.copy(
                status = TimerStatus.IDLE,
                remainingSeconds = total,
                totalSeconds = total,
                endFeedback = ""
            )
        }
    }

    fun onConfigChanged() {
        val s = _state.value
        if (s.status == TimerStatus.IDLE || s.status == TimerStatus.FINISHED) {
            reset()
        }
    }

    fun toggleThemeQuick() {
        settingsRepo.update { cfg ->
            val next = when (cfg.themeMode) {
                ThemeMode.DARK_CYBER -> ThemeMode.LIGHT
                ThemeMode.LIGHT -> ThemeMode.DARK_CYBER
                ThemeMode.SYSTEM -> ThemeMode.DARK_CYBER
            }
            cfg.copy(themeMode = next)
        }
    }

    fun spotifyPlayPause() = spotify.playPause()
    fun spotifySkip() = spotify.skip()

    private fun play() {
        if (_state.value.remainingSeconds <= 0) {
            reset()
        }
        _state.update { it.copy(status = TimerStatus.RUNNING, endFeedback = "") }
        startTicker()
    }

    private fun pause() {
        stopTicker()
        _state.update { it.copy(status = TimerStatus.PAUSED) }
    }

    private fun restartAndPlay() {
        reset()
        play()
    }

    private fun startTicker() {
        stopTicker()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val cur = _state.value
                if (cur.status != TimerStatus.RUNNING) break
                val next = cur.remainingSeconds - 1
                if (next <= 0) {
                    _state.update {
                        it.copy(
                            remainingSeconds = 0,
                            status = TimerStatus.FINISHED,
                            tick = it.tick + 1
                        )
                    }
                    onFinished()
                    break
                } else {
                    _state.update {
                        it.copy(remainingSeconds = next, tick = it.tick + 1)
                    }
                }
            }
        }
    }

    private fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }

    private fun feedbackFor(done: TimerMode): String = when (done) {
        TimerMode.FOCUS -> Strings.FEEDBACK_FOCUS_DONE
        TimerMode.BREAK_SHORT -> Strings.FEEDBACK_SHORT_DONE
        TimerMode.BREAK_LONG -> Strings.FEEDBACK_LONG_DONE
    }

    private fun onFinished() {
        stopTicker()
        val s = _state.value
        val cfg = settingsRepo.config.value
        val feedback = feedbackFor(s.mode)
        if (s.mode == TimerMode.FOCUS) {
            historyRepo.add(
                FocusSession(
                    id = Random.nextLong().toString(),
                    name = s.sessionName.ifBlank { Strings.SESSION_EMPTY },
                    mode = s.mode,
                    plannedSeconds = s.totalSeconds,
                    completedSeconds = s.totalSeconds,
                    finishedAtEpochMs = currentTimeMs(),
                    completed = true
                )
            )
            if (cfg.pauseSpotifyOnFocusEnd && (spotify.connected.value || cfg.spotifyConnected)) {
                spotify.pauseAtFocusEnd()
            }
        }
        _state.update { it.copy(endFeedback = feedback, status = TimerStatus.IDLE) }
    }

    override fun onCleared() {
        stopTicker()
        super.onCleared()
    }
}

expect fun currentTimeMs(): Long
