package com.foco.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foco.app.navigation.Routes
import com.foco.app.ui.auth.LoginScreen
import com.foco.app.ui.config.ConfigScreen
import com.foco.app.ui.history.HistoryScreen
import com.foco.app.ui.theme.FocoTheme
import com.foco.app.ui.timer.TimerScreen

@Composable
fun App(
    container: AppContainer,
    reduceMotion: Boolean = false
) {
    val config by container.settingsRepo.config.collectAsState()
    FocoTheme(themeMode = config.themeMode) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Routes.TIMER
        ) {
            composable(Routes.TIMER) {
                TimerScreen(
                    viewModel = container.timerViewModel,
                    reduceMotion = reduceMotion,
                    onOpenLogin = { navController.navigate(Routes.LOGIN) },
                    onOpenHistory = { navController.navigate(Routes.HISTORY) },
                    onOpenConfig = { navController.navigate(Routes.CONFIG) }
                )
            }
            composable(Routes.HISTORY) {
                HistoryScreen(
                    historyRepo = container.historyRepo,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.CONFIG) {
                ConfigScreen(
                    viewModel = container.configViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onContinueGuest = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
