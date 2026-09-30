package com.foco.app.data

import com.foco.app.BuildConfig
import com.foco.app.data.spotify.AndroidSpotifyController

private var appContextForSpotify: android.content.Context? = null

fun initSpotifyContext(context: android.content.Context) {
    appContextForSpotify = context.applicationContext
}

actual fun createSpotifyController(): SpotifyController {
    val ctx = appContextForSpotify
        ?: error("Call initSpotifyContext() before createSpotifyController()")
    val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    return if (clientId.isBlank()) {
        SpotifyStubController()
    } else {
        AndroidSpotifyController(ctx, clientId)
    }
}
