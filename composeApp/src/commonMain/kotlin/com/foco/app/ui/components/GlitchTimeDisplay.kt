package com.foco.app.ui.components

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foco.app.ui.theme.focoColors
import kotlinx.coroutines.delay
import kotlin.random.Random

fun formatTimer(totalSeconds: Int): String {
    val s = totalSeconds.coerceAtLeast(0)
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    fun two(n: Int) = if (n < 10) "0$n" else "$n"
    return if (h > 0) "$h:${two(m)}:${two(sec)}" else "${two(m)}:${two(sec)}"
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
    val c = focoColors()
    val text = formatTimer(seconds)
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(tick, reduceMotion, running) {
        if (reduceMotion || !running) {
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            return@LaunchedEffect
        }
        if (tick % 7L == 0L && tick > 0) {
            offsetX.snapTo(Random.nextInt(-3, 4).toFloat())
            offsetY.snapTo(Random.nextInt(-2, 3).toFloat())
            delay(60)
            offsetX.animateTo(0f, tween(80))
            offsetY.animateTo(0f, tween(80))
        }
    }

    val scanProgress = if (!reduceMotion && running) {
        val t = rememberInfiniteTransition(label = "hud-scan")
        val p by t.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "scan"
        )
        p
    } else {
        0f
    }

    val density = LocalDensity.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                // Grid + surface
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
                // Inner cyan hairline
                drawRect(
                    color = c.accent3.copy(alpha = if (running) 0.55f else 0.35f),
                    style = Stroke(width = 1.dp.toPx())
                )
                // Outer neon border
                drawRect(color = c.border, style = Stroke(width = 4.dp.toPx()))
                if (running && !reduceMotion) {
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                if (!reduceMotion && running) {
                    Text(
                        text = text,
                        color = c.glitchA.copy(alpha = 0.45f),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-2).sp,
                        modifier = Modifier.offset(
                            x = with(density) { (offsetX.value - 2).toDp() },
                            y = with(density) { offsetY.value.toDp() }
                        )
                    )
                    Text(
                        text = text,
                        color = c.glitchB.copy(alpha = 0.35f),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = (-2).sp,
                        modifier = Modifier.offset(
                            x = with(density) { (offsetX.value + 2).toDp() },
                            y = with(density) { offsetY.value.toDp() }
                        )
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
                        .offset(
                            x = with(density) { offsetX.value.toDp() },
                            y = with(density) { offsetY.value.toDp() }
                        )
                        .then(
                            if (!reduceMotion) {
                                Modifier.drawWithContent {
                                    drawContent()
                                    // Subtle scanlines over digits
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
                            } else Modifier
                        )
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
