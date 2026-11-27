package com.firebaseapp.controlefinanceiro

import android.app.ComponentCaller
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.firebaseapp.controlefinanceiro.helpers.notifications.CHANNEL_ID
import com.firebaseapp.controlefinanceiro.helpers.notifications.ROUTE_EXTRA
import com.firebaseapp.controlefinanceiro.helpers.notifications.createChannel
import com.firebaseapp.controlefinanceiro.ui.MoneyManagerApp
import com.firebaseapp.controlefinanceiro.ui.theme.MoneyManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: ")
        enableEdgeToEdge()

        createChannel(this)

        val initialRoute = getInitialRoute()

        setContent {
            MoneyManagerTheme {
                MoneyManagerApp(initialRoute)
            }
        }
    }

    private fun getInitialRoute(): Int? {
        val initialRoute = intent?.extras?.getInt(ROUTE_EXTRA)
        intent?.removeExtra(ROUTE_EXTRA)
        return initialRoute
    }


}
