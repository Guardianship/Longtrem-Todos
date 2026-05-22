package com.junelin.longtermtodos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.junelin.longtermtodos.biometric.BiometricGuardScreen
import com.junelin.longtermtodos.data.repository.SettingsRepository
import com.junelin.longtermtodos.ui.navigation.TodoNavHost
import com.junelin.longtermtodos.ui.theme.LongTermTodosTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkMode by settingsRepository.darkMode.collectAsState(initial = "system")
            val themeColor by settingsRepository.themeColor.collectAsState(initial = null)
            val dynamicColor by settingsRepository.dynamicColor.collectAsState(initial = false)
            val biometricLockEnabled by settingsRepository.biometricLock.collectAsState(initial = false)
            var isAuthenticated by remember { mutableStateOf(false) }

            LongTermTodosTheme(
                darkMode = darkMode,
                themeColor = themeColor ?: "sage",
                dynamicColor = dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (biometricLockEnabled && !isAuthenticated) {
                        BiometricGuardScreen(
                            onAuthenticated = { isAuthenticated = true }
                        )
                    }

                    AnimatedVisibility(
                        visible = !biometricLockEnabled || isAuthenticated,
                        enter = fadeIn()
                    ) {
                        TodoNavHost()
                    }
                }
            }
        }
    }
}
