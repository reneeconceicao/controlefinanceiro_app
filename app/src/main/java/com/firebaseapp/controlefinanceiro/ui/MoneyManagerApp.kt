package com.firebaseapp.controlefinanceiro.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.firebaseapp.controlefinanceiro.ui.navigation.AppDestination
import com.firebaseapp.controlefinanceiro.ui.navigation.AppNavHost

@Composable
fun MoneyManagerApp(
    initialRoute: Int? = null
) {
    Log.d("TAG_", "MoneyManagerApp: $initialRoute")
    val navController = rememberNavController()

    val route = when (initialRoute) {
        null -> AppDestination.MAIN.route
        0 -> "register/0"
        1 -> "register/1"
        else -> AppDestination.MAIN.route
    }

    Log.d("TAG_", "MoneyManagerApp: $route")
    AppNavHost(navController = navController, startRoute = route)

}