package com.foco.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foco.app.ui.theme.FocoColors
import com.foco.app.ui.theme.LocalReduceMotion
import com.foco.app.ui.theme.focoColors

/** Scanline + corner glow page background matching web body. */
fun Modifier.focoScrim(colors: FocoColors): Modifier = this.drawBehind {
    drawRect(colors.bg)
    // Accent glow top-left
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(colors.accent.copy(alpha = 0.12f), Color.Transparent),
            center = Offset(size.width * 0.2f, 0f),
            radius = size.minDimension * 0.7f
        )
    )
    // Accent-2 glow top-right
    drawRect(
        brush = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(colors.accent2.copy(alpha = 0.10f), Color.Transparent),
            center = Offset(size.width * 0.9f, size.height * 0.08f),
            radius = size.minDimension * 0.55f
        )
    )
    // Horizontal scanlines
    val step = 4.dp.toPx()
    var y = 0f
    val line = colors.accent.copy(alpha = if (colors.isDark) 0.04f else 0.03f)
    while (y < size.height) {
        drawLine(line, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += step
    }
}

/** Material compact/expanded breakpoint — phone portrait is compact. */
val FocoCompactWidthMax = 600.dp

/** Centered panel max width (web-like) for tablet / landscape. */
val FocoPanelMaxWidth = 448.dp

/** Phone edge padding (~16–20dp). */
val FocoPhonePadding = 18.dp

/**
 * Page shell with width breakpoint:
 * - compact (phone): edge-to-safe-area, full-bleed — no outer frame / L-corners
 * - expanded (tablet/landscape): centered neon HudCard panel (web-like)
 */
@Composable
fun FocoPage(
    modifier: Modifier = Modifier,
    centerContent: Boolean = false,
    scrollable: Boolean = true,
    belowPanel: (@Composable ColumnScope.(compact: Boolean) -> Unit)? = null,
    content: @Composable ColumnScope.(compact: Boolean) -> Unit
) {
    val c = focoColors()
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .focoScrim(c)
    ) {
        val compact = maxWidth < FocoCompactWidthMax
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(
                    horizontal = if (compact) FocoPhonePadding else 12.dp,
                    vertical = if (compact) 16.dp else 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nested constraints so Arrangement.Center works with verticalScroll
            // (scrollables otherwise shrink-wrap content and ignore vertical arrangement).
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val bodyMod = when {
                    scrollable -> Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = maxHeight)
                    centerContent -> Modifier.fillMaxSize()
                    else -> Modifier.fillMaxWidth()
                }
                Column(
                    modifier = bodyMod,
                    horizontalAlignment = if (centerContent) {
                        Alignment.CenterHorizontally
                    } else {
                        Alignment.Start
                    },
                    verticalArrangement = if (centerContent) Arrangement.Center else Arrangement.Top
                ) {
                    if (compact) {
                        content(true)
                    } else {
                        HudCard(modifier = Modifier.widthIn(max = FocoPanelMaxWidth)) {
                            content(false)
                        }
                    }
                    belowPanel?.invoke(this, compact)
                }
            }
        }
    }
}

/** Neon outer glow via layered strokes (Compose has no CSS box-shadow). */
fun Modifier.neonBorder(
    color: Color,
    width: Dp = 3.dp,
    glow: Boolean = true
): Modifier = this.drawBehind {
    val stroke = width.toPx()
    if (glow) {
        drawRect(
            color = color.copy(alpha = 0.22f),
            topLeft = Offset(-6f, -6f),
            size = Size(size.width + 12f, size.height + 12f),
            style = Stroke(width = stroke + 10f)
        )
        drawRect(
            color = color.copy(alpha = 0.35f),
            topLeft = Offset(-3f, -3f),
            size = Size(size.width + 6f, size.height + 6f),
            style = Stroke(width = stroke + 4f)
        )
    }
    drawRect(color = color, style = Stroke(width = stroke))
}

/**
 * Web parity for `.page::before/::after` shell-glitch-a/b:
 * duplicate offset strokes in glitch-a (#ff2a6d) / glitch-b (#05d9e8) /
 * glitch-c (#f9f002) with stepped jitter + partial clips.
 * When [enabled] is false (reduced motion), draws nothing — static neon only.
 */
@Composable
fun Modifier.shellGlitchBorder(
    glitchA: Color,
    glitchB: Color,
    glitchC: Color,
    enabled: Boolean,
    width: Dp = 3.dp
): Modifier {
    // Hooks always run; [enabled] only gates drawing (reduced-motion → static neon).
    val transition = rememberInfiniteTransition(label = "shell-glitch")
    val phaseA by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shell-a"
    )
    val phaseB by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1750, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shell-b"
    )
    if (!enabled) return this
    return this.drawWithContent {
        drawContent()
        val stroke = width.toPx()
        val inset = 3.dp.toPx()

        // shell-glitch-a keyframes (steps feel via discrete bands)
        val burstA = phaseA >= 0.86f && phaseA < 0.94f
        val (oxA, oyA, clipA) = when {
            !burstA -> Triple(0f, 0f, Rect(0f, 0f, size.width, size.height))
            phaseA < 0.88f -> Triple(-2.dp.toPx(), 1.dp.toPx(), Rect(0f, 0f, size.width, size.height * 0.28f))
            phaseA < 0.90f -> Triple(3.dp.toPx(), -1.dp.toPx(), Rect(0f, size.height * 0.28f, size.width, size.height * 0.60f))
            phaseA < 0.92f -> Triple(-3.dp.toPx(), 2.dp.toPx(), Rect(0f, size.height * 0.62f, size.width, size.height * 0.92f))
            else -> Triple(2.dp.toPx(), -2.dp.toPx(), Rect(0f, size.height * 0.08f, size.width * 0.35f, size.height * 0.92f))
        }
        // At rest: faint screen-blend fringe; during burst: strong offset/clipped stroke
        val alphaA = if (burstA) 0.70f else 0.28f
        clipRect(clipA.left, clipA.top, clipA.right, clipA.bottom) {
            drawRect(
                color = glitchA.copy(alpha = alphaA),
                topLeft = Offset(oxA - inset, oyA - inset),
                size = Size(size.width + inset * 2f, size.height + inset * 2f),
                style = Stroke(width = stroke),
                blendMode = BlendMode.Screen
            )
        }

        // shell-glitch-b (+ occasional glitch-c border)
        val burstB = phaseB >= 0.84f && phaseB < 0.93f
        val useC = phaseB in 0.84f..0.87f
        val (oxB, oyB, clipB) = when {
            !burstB -> Triple(0f, 0f, Rect(0f, 0f, size.width, size.height))
            phaseB < 0.87f -> Triple(3.dp.toPx(), -1.dp.toPx(), Rect(0f, 0f, size.width, size.height))
            phaseB < 0.89f -> Triple(-4.dp.toPx(), 1.dp.toPx(), Rect(0f, size.height * 0.18f, size.width, size.height * 0.52f))
            phaseB < 0.91f -> Triple(2.dp.toPx(), 2.dp.toPx(), Rect(0f, size.height * 0.55f, size.width, size.height * 0.90f))
            else -> Triple(-2.dp.toPx(), -1.dp.toPx(), Rect(size.width * 0.58f, size.height * 0.10f, size.width, size.height * 0.90f))
        }
        val alphaB = if (burstB) 0.55f else 0.22f
        val colorB = if (useC) glitchC.copy(alpha = alphaB) else glitchB.copy(alpha = alphaB)
        clipRect(clipB.left, clipB.top, clipB.right, clipB.bottom) {
            drawRect(
                color = colorB,
                topLeft = Offset(oxB - inset, oyB - inset),
                size = Size(size.width + inset * 2f, size.height + inset * 2f),
                style = Stroke(width = stroke),
                blendMode = BlendMode.Screen
            )
            drawRect(
                color = glitchC.copy(alpha = if (burstB) 0.35f else 0.12f),
                topLeft = Offset(oxB - inset - 1f, oyB - inset - 1f),
                size = Size(size.width + inset * 2f + 2f, size.height + inset * 2f + 2f),
                style = Stroke(width = 1f),
                blendMode = BlendMode.Screen
            )
        }
    }
}

@Composable
fun HudCard(
    modifier: Modifier = Modifier,
    glow: Boolean = true,
    glitch: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val c = focoColors()
    val reduceMotion = LocalReduceMotion.current
    val shellGlitch = glitch && !reduceMotion
    // reduceMotion is fixed for the Activity; branch is composition-stable.
    val glitchMod = if (shellGlitch) {
        Modifier.shellGlitchBorder(
            glitchA = c.glitchA,
            glitchB = c.glitchB,
            glitchC = c.glitchC,
            enabled = true,
            width = 3.dp
        )
    } else {
        Modifier
    }
    Column(
        modifier = modifier
            .widthIn(max = 448.dp)
            .fillMaxWidth()
            .background(c.surface.copy(alpha = 0.55f), RectangleShape)
            .neonBorder(c.border, width = 3.dp, glow = glow)
            .then(glitchMod)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun HudButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    val c = focoColors()
    val bg = if (primary) c.accent else Color.Transparent
    val fg = if (primary) c.accentFg else c.fg
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .background(bg, RectangleShape)
            .then(
                if (primary) Modifier.neonBorder(c.accent, width = 3.dp, glow = true)
                else Modifier.border(3.dp, c.border, RectangleShape)
            )
            .semantics {
                role = Role.Button
                contentDescription?.let { this.contentDescription = it }
            }
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = if (enabled) fg else c.muted,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.2.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun HudModeTab(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val c = focoColors()
    Box(
        modifier = modifier
            .heightIn(min = 36.dp)
            .drawBehind {
                val path = Path().apply {
                    val cut = 10.dp.toPx()
                    moveTo(0f, 0f)
                    lineTo(size.width - cut, 0f)
                    lineTo(size.width, cut)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path, c.accent)
                drawPath(path, c.border, style = Stroke(width = 3.dp.toPx()))
            }
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            color = c.accentFg,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
fun HudTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true
) {
    val c = focoColors()
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = TextStyle(
            color = c.fg,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif
        ),
        cursorBrush = SolidColor(c.accent),
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, c.border, RectangleShape)
            .background(c.surface.copy(alpha = 0.4f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = c.muted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                inner()
            }
        }
    )
}

@Composable
fun HudSectionLabel(text: String, modifier: Modifier = Modifier) {
    val c = focoColors()
    Text(
        text = text.uppercase(),
        color = c.muted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = modifier
    )
}

@Composable
fun HudTitle(text: String, modifier: Modifier = Modifier) {
    val c = focoColors()
    Text(
        text = text.uppercase(),
        color = c.accent,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = modifier
    )
}

@Composable
fun HudLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = focoColors()
    Text(
        text = text.uppercase(),
        color = c.fg.copy(alpha = 0.85f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        textDecoration = TextDecoration.Underline,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(4.dp)
    )
}

@Composable
fun RowScope.HudButtonRowItem(
    text: String,
    onClick: () -> Unit,
    primary: Boolean = false,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    HudButton(
        text = text,
        onClick = onClick,
        primary = primary,
        enabled = enabled,
        contentDescription = contentDescription,
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun BrandMark(modifier: Modifier = Modifier, size: Dp = 36.dp) {
    // Image loaded via BrandLogo composable that uses compose resources when available.
    BrandLogo(modifier = modifier.size(size))
}
