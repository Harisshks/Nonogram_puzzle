package com.nonogram.puzzle

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.nonogram.puzzle.navigation.NonogramNavGraph
import com.nonogram.puzzle.navigation.Screen
import com.nonogram.puzzle.ui.theme.AppBackground
import com.nonogram.puzzle.ui.theme.LocalDarkMode
import com.nonogram.puzzle.ui.theme.NonogramTheme
import com.nonogram.puzzle.ui.theme.PrimaryBlue
import com.nonogram.puzzle.utils.SoundManager
import com.nonogram.puzzle.viewmodel.SettingsViewModel
import com.nonogram.puzzle.viewmodel.TutorialViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onDestroy() {
        SoundManager.getInstance(applicationContext).release()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.setBackgroundDrawable(ColorDrawable(AppBackground.toArgb()))
        setContent {
            val tutorialViewModel: TutorialViewModel = viewModel()
            val tutorialDone by tutorialViewModel.tutorialCompleted.collectAsStateWithLifecycle()
            var isReady by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(200L)
                isReady = true
            }

            val settingsViewModel: SettingsViewModel = viewModel()
            val darkMode by settingsViewModel.darkMode.collectAsStateWithLifecycle()
            CompositionLocalProvider(LocalDarkMode provides darkMode) {
                NonogramTheme(darkTheme = darkMode) {
                    val navController = rememberNavController()
                    if (!isReady) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        NonogramNavGraph(
                            navController = navController,
                            startDestination = if (!tutorialDone) {
                                Screen.Tutorial.route
                            } else {
                                Screen.Home.route
                            }
                        )
                    }
                }
            }
        }
    }
}
