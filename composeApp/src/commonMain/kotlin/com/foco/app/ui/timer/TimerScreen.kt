package com.foco.app.ui.timer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foco.app.domain.TimerStatus
import com.foco.app.domain.TimerViewModel
import com.foco.app.ui.Strings
import com.foco.app.ui.components.BrandLogo
import com.foco.app.ui.components.GlitchTimeDisplay
import com.foco.app.ui.components.HudButton
import com.foco.app.ui.components.HudCard
import com.foco.app.ui.components.HudLink
import com.foco.app.ui.components.HudModeTab
import com.foco.app.ui.components.HudSectionLabel
import com.foco.app.ui.components.HudTextField
import com.foco.app.ui.components.SpotifyMiniPlayer
import com.foco.app.ui.components.focoScrim
import com.foco.app.ui.theme.focoColors

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    reduceMotion: Boolean,
    onOpenLogin: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val config by viewModel.config.collectAsState()
    val spotifyOn by viewModel.spotifyConnected.collectAsState()
    val track by viewModel.nowPlaying.collectAsState()
    val c = focoColors()
    val running = state.status == TimerStatus.RUNNING

    Box(
        modifier = modifier
            .fillMaxSize()
            .focoScrim(c)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HudCard(modifier = Modifier.widthIn(max = 448.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BrandLogo(modifier = Modifier.size(36.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = Strings.APP_NAME,
                            color = c.fg,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val themeA11y =
                            if (c.isDark) Strings.A11Y_THEME_LIGHT else Strings.A11Y_THEME_DARK
                        Text(
                            text = if (c.isDark) "☀" else "☾",
                            color = c.fg.copy(alpha = 0.85f),
                            fontSize = 16.sp,
                            modifier = Modifier
                                .semantics { contentDescription = themeA11y }
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = { viewModel.toggleThemeQuick() }
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                        HudLink(text = Strings.LOGIN, onClick = onOpenLogin)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .drawBehind {
                            drawRect(c.border, size = Size(size.width, 3.dp.toPx()))
                            drawRect(
                                c.accent2.copy(alpha = 0.4f),
                                topLeft = Offset(0f, 3.dp.toPx()),
                                size = Size(size.width, 3.dp.toPx())
                            )
                        }
                )
                Spacer(Modifier.height(10.dp))

                HudModeTab(
                    label = state.mode.label,
                    onClick = { viewModel.cycleMode() },
                    enabled = !running
                )

                Spacer(Modifier.height(10.dp))

                GlitchTimeDisplay(
                    seconds = state.remainingSeconds,
                    tick = state.tick,
                    reduceMotion = reduceMotion,
                    statusLabel = state.statusLabel,
                    running = running,
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.endFeedback.isNotBlank()) {
                    Text(
                        text = state.endFeedback,
                        color = c.accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HudButton(
                        text = if (running) Strings.PAUSE else Strings.PLAY,
                        onClick = { viewModel.playPause() },
                        primary = true,
                        contentDescription = if (running) Strings.A11Y_PAUSE else Strings.A11Y_PLAY,
                        modifier = Modifier.weight(1f)
                    )
                    HudButton(
                        text = Strings.RESET,
                        onClick = { viewModel.reset() },
                        primary = false,
                        contentDescription = Strings.A11Y_RESET,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                HudSectionLabel(Strings.SESSION)
                Spacer(Modifier.height(4.dp))
                HudTextField(
                    value = state.sessionName,
                    onValueChange = viewModel::setSessionName,
                    placeholder = Strings.SESSION_PLACEHOLDER
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HudButton(
                        text = Strings.HISTORY,
                        onClick = onOpenHistory,
                        primary = false,
                        modifier = Modifier.weight(1f)
                    )
                    HudButton(
                        text = Strings.CONFIG,
                        onClick = onOpenConfig,
                        primary = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (spotifyOn || config.spotifyConnected) {
                Spacer(Modifier.height(12.dp))
                SpotifyMiniPlayer(
                    track = track,
                    onPlayPause = viewModel::spotifyPlayPause,
                    onSkip = viewModel::spotifySkip,
                    modifier = Modifier
                        .widthIn(max = 448.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
