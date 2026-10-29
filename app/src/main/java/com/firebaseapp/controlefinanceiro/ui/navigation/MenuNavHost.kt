package com.firebaseapp.controlefinanceiro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.firebaseapp.controlefinanceiro.ui.screens.budgets.BudgetScreen
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeScreen
import com.firebaseapp.controlefinanceiro.ui.screens.reports.ReportScreen
import com.firebaseapp.controlefinanceiro.ui.screens.settings.SettingsScreen

@Composable
fun MenuNavHost(
    navController: NavHostController,
    startDestination: MenuDestination,
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
    navigateToEdit: (Int) -> Unit,
    navigateToEditRecurring: (Int, Long?) -> Unit,
    navigateExpenseCategories: () -> Unit,
    navigateIncomeCategories: () -> Unit,
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        MenuDestination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    MenuDestination.HOME -> HomeScreen(
                        modifier = modifier,
                        navigateToRegister = navigateToRegister,
                        navigateToEdit = navigateToEdit,
                        navigateToEditRecurring = navigateToEditRecurring,
                    )

                    MenuDestination.BUDGET -> BudgetScreen(modifier = modifier, {}, {})
                    MenuDestination.REPORT -> ReportScreen(modifier = modifier)
                    MenuDestination.SETTINGS -> SettingsScreen(
                        modifier = modifier,
                        navigateExpenseCategories = navigateExpenseCategories,
                        navigateIncomeCategories = navigateIncomeCategories

                    )
                }
            }
        }
    }
}


