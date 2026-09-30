package com.foco.app.ui.config

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foco.app.domain.ConfigViewModel
import com.foco.app.domain.DurationUnit
import com.foco.app.domain.ThemeMode
import com.foco.app.ui.Strings
import com.foco.app.ui.components.HudButton
import com.foco.app.ui.components.HudCard
import com.foco.app.ui.components.HudLink
import com.foco.app.ui.components.HudSectionLabel
import com.foco.app.ui.components.HudTextField
import com.foco.app.ui.components.HudTitle
import com.foco.app.ui.components.focoScrim
import com.foco.app.ui.theme.focoColors

@Composable
fun ConfigScreen(
    viewModel: ConfigViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config by viewModel.config.collectAsState()
    val spotifyOn by viewModel.spotifyConnected.collectAsState()
    val summary by viewModel.spotifySummary.collectAsState()
    val status by viewModel.spotifyStatus.collectAsState()
    val c = focoColors()
    val connected = spotifyOn || config.spotifyConnected

    Box(
        modifier = modifier
            .fillMaxSize()
            .focoScrim(c)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HudCard(modifier = Modifier.widthIn(max = 448.dp)) {
                HudLink(text = Strings.BACK_TIMER, onClick = onBack)
                Spacer(Modifier.height(8.dp))
                HudTitle(Strings.CONFIG_TITLE)
                Spacer(Modifier.height(16.dp))

                HudSectionLabel(Strings.DURATIONS)
                Spacer(Modifier.height(6.dp))
                Text(
                    Strings.DURATION_HINT,
                    color = c.muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DurationUnit.entries.forEach { unit ->
                        UnitChip(
                            label = unit.label,
                            selected = config.durationUnit == unit,
                            onClick = { viewModel.setDurationUnit(unit) }
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))

                DurationField(
                    label = Strings.LABEL_FOCUS,
                    value = viewModel.displayValue(config.focusDurationSeconds).toString(),
                    unit = config.durationUnit.label,
                    onChange = { raw ->
                        raw.filter { it.isDigit() }.toIntOrNull()?.let {
                            viewModel.setFocusDurationFromDisplay(it)
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
                DurationField(
                    label = Strings.LABEL_BREAK_SHORT,
                    value = viewModel.displayValue(config.breakDurationSeconds).toString(),
                    unit = config.durationUnit.label,
                    onChange = { raw ->
                        raw.filter { it.isDigit() }.toIntOrNull()?.let {
                            viewModel.setBreakDurationFromDisplay(it)
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
                DurationField(
                    label = Strings.LABEL_BREAK_LONG,
                    value = viewModel.displayValue(config.longBreakDurationSeconds).toString(),
                    unit = config.durationUnit.label,
                    onChange = { raw ->
                        raw.filter { it.isDigit() }.toIntOrNull()?.let {
                            viewModel.setLongBreakDurationFromDisplay(it)
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))
                HudSectionLabel(Strings.ALERTA)
                Spacer(Modifier.height(8.dp))
                HudToggle(Strings.SOUND, config.soundEnabled) { viewModel.setSound(it) }
                Spacer(Modifier.height(6.dp))
                HudToggle(Strings.NOTIFICATIONS, config.notificationsEnabled) {
                    viewModel.setNotifications(it)
                }

                Spacer(Modifier.height(16.dp))
                HudSectionLabel(Strings.THEME)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UnitChip(
                        label = Strings.THEME_DARK,
                        selected = config.themeMode == ThemeMode.DARK_CYBER ||
                            (config.themeMode == ThemeMode.SYSTEM && c.isDark),
                        onClick = { viewModel.setTheme(ThemeMode.DARK_CYBER) }
                    )
                    UnitChip(
                        label = Strings.THEME_LIGHT,
                        selected = config.themeMode == ThemeMode.LIGHT ||
                            (config.themeMode == ThemeMode.SYSTEM && !c.isDark),
                        onClick = { viewModel.setTheme(ThemeMode.LIGHT) }
                    )
                }

                Spacer(Modifier.height(16.dp))
                HudSectionLabel(Strings.DEFAULT_SESSION_NAME)
                Spacer(Modifier.height(4.dp))
                HudTextField(
                    value = config.defaultSessionName.ifBlank { config.displayName },
                    onValueChange = {
                        viewModel.setDefaultSessionName(it)
                        viewModel.setDisplayName(it)
                    },
                    placeholder = Strings.SESSION_PLACEHOLDER
                )

                Spacer(Modifier.height(16.dp))
                HudSectionLabel(Strings.SPOTIFY)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (connected) Strings.SPOTIFY_CONNECTED else Strings.SPOTIFY_DISCONNECTED,
                    color = if (connected) c.accent else c.muted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (connected && !summary.isNullOrBlank()) {
                    Text(summary!!, color = c.muted, fontSize = 12.sp)
                }
                status?.let { msg ->
                    Text(msg, color = c.muted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(8.dp))
                HudButton(
                    text = if (connected) Strings.DISCONNECT_SPOTIFY else Strings.CONNECT_SPOTIFY,
                    onClick = {
                        if (connected) viewModel.disconnectSpotify()
                        else viewModel.connectSpotify()
                    },
                    primary = !connected,
                    modifier = Modifier.fillMaxWidth()
                )
                if (connected) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        Strings.SPOTIFY_MINIPLAYER_HINT,
                        color = c.muted,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                HudToggle(Strings.PAUSE_ON_FOCUS_END, config.pauseSpotifyOnFocusEnd) {
                    viewModel.setPauseSpotifyOnEnd(it)
                }
            }
        }
    }
}

@Composable
private fun DurationField(
    label: String,
    value: String,
    unit: String,
    onChange: (String) -> Unit
) {
    val c = focoColors()
    Column {
        Text(
            "$label · $unit",
            color = c.fg,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        HudTextField(value = value, onValueChange = onChange, placeholder = "0")
    }
}

@Composable
private fun UnitChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val c = focoColors()
    Box(
        modifier = Modifier
            .background(if (selected) c.accent else c.surface, RectangleShape)
            .border(2.dp, c.border, RectangleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = if (selected) c.accentFg else c.fg,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun HudToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    val c = focoColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onChange(!checked) }
            )
            .border(2.dp, c.border, RectangleShape)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = c.fg, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Box(
            modifier = Modifier
                .background(if (checked) c.accent else c.surface2, RectangleShape)
                .border(2.dp, c.border, RectangleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                if (checked) "ON" else "OFF",
                color = if (checked) c.accentFg else c.muted,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
