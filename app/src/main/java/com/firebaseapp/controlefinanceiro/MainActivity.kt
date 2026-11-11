package com.firebaseapp.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.firebaseapp.controlefinanceiro.ui.MoneyManagerApp
import com.firebaseapp.controlefinanceiro.ui.theme.MoneyManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyManagerTheme {
                MoneyManagerApp()
            }
        }
    }
}
