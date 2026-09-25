package com.firebaseapp.controlefinanceiro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.firebaseapp.controlefinanceiro.ui.screens.budgets.BudgetsScreen
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeScreen
import com.firebaseapp.controlefinanceiro.ui.screens.reports.ReportScreen
import com.firebaseapp.controlefinanceiro.ui.screens.settings.SettingsScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: MainDestination,
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        MainDestination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    MainDestination.HOME -> HomeScreen(modifier = modifier, navigateToRegister = navigateToRegister)

                    MainDestination.BUDGET -> BudgetsScreen(modifier = modifier)
                    MainDestination.REPORT -> ReportScreen(modifier = modifier)
                    MainDestination.SETTINGS -> SettingsScreen(modifier = modifier)
                }
            }
        }
    }
}
