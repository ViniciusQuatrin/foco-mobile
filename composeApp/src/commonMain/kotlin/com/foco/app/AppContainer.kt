package com.foco.app

import com.foco.app.data.SessionHistoryRepository
import com.foco.app.data.SettingsRepository
import com.foco.app.data.SpotifyController
import com.foco.app.data.createSettings
import com.foco.app.data.createSpotifyController
import com.foco.app.domain.ConfigViewModel
import com.foco.app.domain.TimerViewModel

class AppContainer {
    val settingsRepo = SettingsRepository(createSettings())
    val historyRepo = SessionHistoryRepository()
    val spotify: SpotifyController = createSpotifyController()

    val timerViewModel by lazy {
        TimerViewModel(settingsRepo, historyRepo, spotify)
    }
    val configViewModel by lazy {
        ConfigViewModel(settingsRepo, spotify)
    }
}
