package com.firebaseapp.controlefinanceiro.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.firebaseapp.controlefinanceiro.ui.navigation.AppDestination
import com.firebaseapp.controlefinanceiro.ui.navigation.AppNavHost

@Composable
fun MoneyManagerApp() {

    val navController = rememberNavController()

    AppNavHost(navController = navController, startDestination = AppDestination.MAIN)

}