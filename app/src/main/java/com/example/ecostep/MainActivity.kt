package com.example.ecostep

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.ecostep.data.AppGraph
import com.example.ecostep.ui.EcoStepApp
import com.example.ecostep.ui.theme.EcoStepTheme

// CompositionLocal pentru Activity - funcționează pe toate dispozitivele
val LocalFragmentActivity = staticCompositionLocalOf<FragmentActivity?> { null }

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppGraph.provide(applicationContext)

        setContent {
            val context = applicationContext
            var isDarkTheme by remember { mutableStateOf(com.example.ecostep.util.PreferencesManager.isDarkMode(context)) }
            
            EcoStepTheme(darkTheme = isDarkTheme) {
                // Transmitem Activity-ul direct prin CompositionLocal
                CompositionLocalProvider(
                    LocalContext provides this@MainActivity,
                    LocalFragmentActivity provides this@MainActivity
                ) {
                    EcoStepApp(
                        isDarkTheme = isDarkTheme,
                        onThemeChanged = { newTheme -> isDarkTheme = newTheme }
                    )
                }
            }
        }
    }
}
