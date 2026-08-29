package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.KiwiCodeScreen
import com.example.ui.KiwiViewModel
import com.example.ui.theme.KiwiCodeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: KiwiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KiwiCodeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KiwiCodeScreen(viewModel = viewModel)
                }
            }
        }
    }
}

