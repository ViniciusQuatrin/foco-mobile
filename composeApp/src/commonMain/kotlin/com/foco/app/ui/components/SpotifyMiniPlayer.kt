package com.foco.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foco.app.data.SpotifyTrack
import com.foco.app.ui.Strings
import com.foco.app.ui.theme.focoColors

@Composable
fun SpotifyMiniPlayer(
    track: SpotifyTrack?,
    onPlayPause: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = focoColors()
    val playing = track?.isPlaying == true
    val hasTrack = track != null && (track.title.isNotBlank() || track.artist.isNotBlank())
    val label = when {
        !hasTrack -> Strings.NOTHING_PLAYING
        else -> {
            val title = track!!.title.ifBlank { "TOCANDO" }
            val artist = track.artist
            if (artist.isBlank()) title.uppercase()
            else "${title.uppercase()} · ${artist.uppercase()}"
        }
    }
    val labelColor = when {
        !hasTrack -> c.muted
        playing -> c.accent
        else -> c.muted
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(c.surface.copy(alpha = 0.55f), RectangleShape)
            .neonBorder(c.border, width = 2.dp, glow = true)
            .padding(12.dp)
            .heightIn(min = 48.dp)
    ) {
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            HudButton(
                text = if (playing) Strings.SPOTIFY_PAUSE_TRACK else Strings.SPOTIFY_PLAY_TRACK,
                onClick = onPlayPause,
                primary = false,
                modifier = Modifier.weight(1f)
            )
            HudButton(
                text = Strings.SKIP,
                onClick = onSkip,
                primary = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
