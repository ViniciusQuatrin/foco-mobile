package com.foco.app.data

import com.foco.app.domain.FocusSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** In-memory history for scaffold; swap for SQLDelight/Room later. */
class SessionHistoryRepository {
    private val _sessions = MutableStateFlow<List<FocusSession>>(emptyList())
    val sessions: StateFlow<List<FocusSession>> = _sessions.asStateFlow()

    fun add(session: FocusSession) {
        _sessions.update { listOf(session) + it }
    }

    fun clear() {
        _sessions.value = emptyList()
    }
}
