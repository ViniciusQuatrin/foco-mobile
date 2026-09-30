package com.foco.app

import android.app.Application
import com.foco.app.data.initSettingsContext
import com.foco.app.data.initSpotifyContext

class FocoApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        initSettingsContext(this)
        initSpotifyContext(this)
        container = AppContainer()
    }
}
