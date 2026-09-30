package com.foco.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
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

@Composable
fun HudCard(
    modifier: Modifier = Modifier,
    glow: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val c = focoColors()
    Column(
        modifier = modifier
            .widthIn(max = 448.dp)
            .fillMaxWidth()
            .background(c.surface.copy(alpha = 0.55f), RectangleShape)
            .neonBorder(c.border, width = 3.dp, glow = glow)
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
