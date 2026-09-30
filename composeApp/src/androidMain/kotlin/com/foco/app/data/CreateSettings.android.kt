package com.foco.app.data

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.Settings

private var appContext: Context? = null

fun initSettingsContext(context: Context) {
    appContext = context.applicationContext
}

actual fun createSettings(): Settings {
    val ctx = appContext ?: error("Call initSettingsContext() before createSettings()")
    return SharedPreferencesSettings(
        ctx.getSharedPreferences("foco_prefs", Context.MODE_PRIVATE)
    )
}
