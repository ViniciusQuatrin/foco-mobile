package com.foco.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foco.app.ui.theme.focoColors
import kotlin.math.roundToInt

fun formatTimer(totalSeconds: Int): String {
    val s = totalSeconds.coerceAtLeast(0)
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    fun two(n: Int) = if (n < 10) "0$n" else "$n"
    return if (h > 0) "$h:${two(m)}:${two(sec)}" else "${two(m)}:${two(sec)}"
}

/**
 * Web `.time-glitch` chromatic layers (glitch-a / glitch-b keyframes).
 * Quiet most of the cycle; brief stepped RGB-split bursts near the end.
 */
private fun glitchKeyframeOffset(phase: Float, variant: Char): Pair<Float, Float> {
    return when (variant) {
        'A' -> when {
            phase < 0.88f || phase >= 0.94f -> 0f to 0f
            phase < 0.90f -> -3f to 1f
            phase < 0.92f -> 3f to -1f
            else -> -2f to 2f
        }
        else -> when {
            phase < 0.86f || phase >= 0.93f -> 0f to 0f
            phase < 0.89f -> 3f to -1f
            phase < 0.91f -> -4f to 1f
            else -> 2f to 2f
        }
    }
}

@Composable
fun GlitchTimeDisplay(
    seconds: Int,
    tick: Long,
    reduceMotion: Boolean,
    statusLabel: String,
    running: Boolean,
    modifier: Modifier = Modifier
) {
    // reduceMotion is Activity-stable → safe to branch away from animation clocks.
    if (reduceMotion) {
        StaticTimeDisplay(
            seconds = seconds,
            statusLabel = statusLabel,
            running = running,
            modifier = modifier
        )
    } else {
        AnimatedGlitchTimeDisplay(
            seconds = seconds,
            tick = tick,
            statusLabel = statusLabel,
            running = running,
            modifier = modifier
        )
    }
}

@Composable
private fun TimerChrome(
    running: Boolean,
    scanProgress: Float?,
    content: @Composable () -> Unit
) {
    val c = focoColors()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(c.surface)
                val step = 24.dp.toPx()
                val vLine = c.accent.copy(alpha = 0.18f)
                val hLine = c.accent2.copy(alpha = 0.12f)
                var x = step / 2f
                while (x < size.width) {
                    drawLine(vLine, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    x += step
                }
                var y = step / 2f
                while (y < size.height) {
                    drawLine(hLine, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    y += step
                }
                drawRect(
                    color = c.accent3.copy(alpha = if (running) 0.55f else 0.35f),
                    style = Stroke(width = 1.dp.toPx())
                )
                // Static neon border (shell colorful glitch lives on HudCard)
                drawRect(color = c.border, style = Stroke(width = 4.dp.toPx()))
                if (scanProgress != null) {
                    val bandH = size.height * 0.35f
                    val top = size.height * scanProgress - bandH / 2f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                c.accent.copy(alpha = 0f),
                                c.accent.copy(alpha = 0.08f),
                                c.accent.copy(alpha = 0f)
                            ),
                            startY = top,
                            endY = top + bandH
                        )
                    )
                }
            }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun StaticTimeDisplay(
    seconds: Int,
    statusLabel: String,
    running: Boolean,
    modifier: Modifier = Modifier
) {
    val c = focoColors()
    val text = formatTimer(seconds)
    Box(modifier = modifier) {
        TimerChrome(running = running, scanProgress = null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = text,
                    color = c.fg,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = statusLabel,
                    color = c.muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedGlitchTimeDisplay(
    seconds: Int,
    tick: Long,
    statusLabel: String,
    running: Boolean,
    modifier: Modifier = Modifier
) {
    val c = focoColors()
    val text = formatTimer(seconds)
    val density = LocalDensity.current
    val animateGlitch = running

    val transition = rememberInfiniteTransition(label = "time-glitch")
    val phaseA by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch-a"
    )
    val phaseB by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1350, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch-b"
    )
    val scanProgress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan"
    )

    val (oxA, oyA) = if (animateGlitch) glitchKeyframeOffset(phaseA, 'A') else 0f to 0f
    val (oxB, oyB) = if (animateGlitch) glitchKeyframeOffset(phaseB, 'B') else 0f to 0f
    val mainJitterX = if (animateGlitch && (oxA != 0f || oxB != 0f)) (oxA + oxB) / 4f else 0f
    val mainJitterY = if (animateGlitch && (oyA != 0f || oyB != 0f)) (oyA + oyB) / 4f else 0f

    @Suppress("UNUSED_VARIABLE")
    val ignoredTick = tick

    Box(modifier = modifier) {
        TimerChrome(
            running = running,
            scanProgress = if (animateGlitch) scanProgress else null
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.clipToBounds()
                ) {
                    if (animateGlitch) {
                        Text(
                            text = text,
                            color = c.glitchA.copy(alpha = 0.75f),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = (-2).sp,
                            modifier = Modifier.offset {
                                IntOffset(
                                    (oxA * density.density).roundToInt(),
                                    (oyA * density.density).roundToInt()
                                )
                            }
                        )
                        Text(
                            text = text,
                            color = c.glitchB.copy(alpha = 0.65f),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = (-2).sp,
                            modifier = Modifier.offset {
                                IntOffset(
                                    (oxB * density.density).roundToInt(),
                                    (oyB * density.density).roundToInt()
                                )
                            }
                        )
                    }
                    Text(
                        text = text,
                        color = c.fg,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-2).sp,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (mainJitterX * density.density).roundToInt(),
                                    (mainJitterY * density.density).roundToInt()
                                )
                            }
                            .drawWithContent {
                                drawContent()
                                var sy = 0f
                                val step = 3.dp.toPx()
                                while (sy < size.height) {
                                    drawLine(
                                        c.bg.copy(alpha = 0.18f),
                                        Offset(0f, sy),
                                        Offset(size.width, sy),
                                        strokeWidth = 1f
                                    )
                                    sy += step
                                }
                            }
                    )
                }
                Text(
                    text = statusLabel,
                    color = c.muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
