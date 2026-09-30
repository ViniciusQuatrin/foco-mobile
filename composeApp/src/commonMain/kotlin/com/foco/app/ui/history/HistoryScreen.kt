package com.foco.app.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foco.app.data.SessionHistoryRepository
import com.foco.app.domain.FocusSession
import com.foco.app.ui.Strings
import com.foco.app.ui.components.FocoPage
import com.foco.app.ui.components.HudLink
import com.foco.app.ui.components.HudTitle
import com.foco.app.ui.components.formatTimer
import com.foco.app.ui.components.neonBorder
import com.foco.app.ui.theme.focoColors

@Composable
fun HistoryScreen(
    historyRepo: SessionHistoryRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessions by historyRepo.sessions.collectAsState()
    val c = focoColors()

    FocoPage(
        modifier = modifier,
        centerContent = false,
        scrollable = true
    ) { compact ->
        val gap = if (compact) 12.dp else 8.dp
        HudLink(text = Strings.BACK_TIMER, onClick = onBack)
        Spacer(Modifier.height(gap))
        HudTitle(Strings.HISTORY_TITLE)
        Spacer(Modifier.height(6.dp))
        Text(
            Strings.HISTORY_NOTE,
            color = c.muted,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(if (compact) 16.dp else 12.dp))

        if (sessions.isEmpty()) {
            Text(
                Strings.HISTORY_EMPTY_TITLE,
                color = c.fg,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                Strings.HISTORY_EMPTY_BODY,
                color = c.muted,
                fontSize = 13.sp
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sessions.forEach { session ->
                    SessionRow(session)
                }
            }
        }
    }
}

@Composable
private fun SessionRow(session: FocusSession) {
    val c = focoColors()
    val name = session.name.ifBlank { Strings.SESSION_EMPTY }
    val line = "$name · ${session.mode.shortLabel} · ${formatTimer(session.completedSeconds)}"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(c.surface.copy(alpha = 0.5f), RectangleShape)
            .neonBorder(c.border, width = 2.dp, glow = false)
            .padding(12.dp)
    ) {
        Text(
            text = line.uppercase(),
            color = c.fg,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}
