package com.foco.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.foco.app.ui.theme.focoColors
import foco.composeapp.generated.resources.Res
import foco.composeapp.generated.resources.logo_40
import foco.composeapp.generated.resources.logo_128
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BrandLogo(
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val c = focoColors()
    val res: DrawableResource = if (large) Res.drawable.logo_128 else Res.drawable.logo_40
    Box(
        modifier = modifier
            .border(2.dp, c.border, RectangleShape)
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(res),
            contentDescription = "FOCO",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
