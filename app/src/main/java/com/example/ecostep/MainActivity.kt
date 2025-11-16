package com.example.ecostep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ecostep.data.AppGraph
import com.example.ecostep.ui.EcoStepApp
import com.example.ecostep.ui.theme.EcoStepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppGraph.init(applicationContext)

        setContent {
            EcoStepTheme {
                EcoStepApp()
            }
        }
    }
}
