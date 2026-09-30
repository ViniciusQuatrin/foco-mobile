package com.foco.app.ui.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foco.app.ui.Strings
import com.foco.app.ui.components.BrandLogo
import com.foco.app.ui.components.FocoPage
import com.foco.app.ui.components.HudButton
import com.foco.app.ui.components.HudLink
import com.foco.app.ui.components.HudSectionLabel
import com.foco.app.ui.components.HudTextField
import com.foco.app.ui.components.HudTitle
import com.foco.app.ui.theme.focoColors

@Composable
fun LoginScreen(
    onContinueGuest: () -> Unit,
    onBack: () -> Unit = onContinueGuest,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val c = focoColors()

    FocoPage(
        modifier = modifier,
        centerContent = true,
        scrollable = true
    ) { compact ->
        val gap = if (compact) 12.dp else 8.dp
        HudLink(text = Strings.BACK_TIMER, onClick = onBack)
        Spacer(Modifier.height(if (compact) 16.dp else 12.dp))
        BrandLogo(
            modifier = Modifier.size(if (compact) 112.dp else 96.dp).align(Alignment.CenterHorizontally),
            large = true
        )
        Spacer(Modifier.height(gap))
        HudTitle(Strings.LOGIN_TITLE, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(8.dp))
        Text(
            Strings.LOGIN_PROMISE,
            color = c.muted,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(if (compact) 20.dp else 16.dp))

        HudSectionLabel(Strings.EMAIL)
        Spacer(Modifier.height(4.dp))
        HudTextField(value = email, onValueChange = { email = it }, placeholder = Strings.EMAIL)
        Spacer(Modifier.height(10.dp))
        HudSectionLabel(Strings.PASSWORD)
        Spacer(Modifier.height(4.dp))
        HudTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = Strings.PASSWORD
        )
        Spacer(Modifier.height(if (compact) 20.dp else 16.dp))
        HudButton(
            text = Strings.LOGIN_SUBMIT,
            onClick = { /* stub auth */ },
            primary = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        HudButton(
            text = Strings.CONTINUE_GUEST,
            onClick = onContinueGuest,
            primary = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
