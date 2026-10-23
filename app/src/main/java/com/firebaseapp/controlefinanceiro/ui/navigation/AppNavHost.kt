package com.firebaseapp.controlefinanceiro.ui.navigation

import android.util.Log
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.firebaseapp.controlefinanceiro.ui.screens.camera.MainCameraScreen
import com.firebaseapp.controlefinanceiro.ui.screens.categories.ExpenseCategoriesScreen
import com.firebaseapp.controlefinanceiro.ui.screens.categories.IncomeCategoriesScreen
import com.firebaseapp.controlefinanceiro.ui.screens.edit.EditScreen
import com.firebaseapp.controlefinanceiro.ui.screens.main.MainScreen
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterScreen


@Composable
fun AppNavHost(navController: NavHostController, startDestination: AppDestination) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        enterTransition = { fadeIn() },
        exitTransition = { ExitTransition.None }) {

        composable(AppDestination.MAIN.route) {
            MainScreen(
                navigateToRegister = { navController.navigate(AppDestination.REGISTER.route) },
                navigateToEdit = {
                    Log.d("TAG", "AppNavHost: ")
                    navController.navigate("${AppDestination.EDIT.route}/${it}")
                },
                navigateToEditRecurring = { id, index ->
                    navController.navigate("${AppDestination.EDIT_RECURRING.route}/${id}/$index")
                },
                navigateExpenseCategories = {
                    navController.navigate(AppDestination.EXPENSE_CATEGORIES.route)
                },
                navigateIncomeCategories = {
                    navController.navigate(AppDestination.INCOME_CATEGORIES.route)
                },
            )
        }

        composable(
            AppDestination.REGISTER.route,
            enterTransition = { slideInHorizontally { it } }) {

            var camera: String? = navController.currentBackStackEntry?.savedStateHandle["photoUri"]

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>("photoUri")


            RegisterScreen(
                navigateBack = { navController.popBackStack() },
                navigateCategories = {
                    val route = if (it == 0) {
                        AppDestination.EXPENSE_CATEGORIES.route
                    } else {
                        AppDestination.INCOME_CATEGORIES.route
                    }
                    navController.navigate(route)
                },
                navigateCamera = {
                    navController.navigate(AppDestination.CAMERA.route)
                },
                resultForCamera = camera,
            )
        }

        composable(
            AppDestination.CAMERA.route,
            enterTransition = { slideInVertically { it } },
            exitTransition = { slideOutVertically { it } }) {
            MainCameraScreen(
                navigateBack = { uri ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("photoUri", uri)

                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }

            )
        }

        composable(
            route = AppDestination.EDIT.routeWithArgs,
            arguments = listOf(navArgument("wordId") {
                type = NavType.IntType
            })
        ) {
            val camera: String? = navController.currentBackStackEntry?.savedStateHandle["photoUri"]

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>("photoUri")

            Log.d("TAG", "AppNavHost: $camera")

            EditScreen(
                navigateBack = { navController.popBackStack() },
                navigateCategories = {
                    val route = if (it == 0) {
                        AppDestination.EXPENSE_CATEGORIES.route
                    } else {
                        AppDestination.INCOME_CATEGORIES.route
                    }
                    navController.navigate(route)
                },
                navigateCamera = {
                    navController.navigate(AppDestination.CAMERA.route)
                },
                resultForCamera = camera
            )
        }

        composable(
            route = AppDestination.EDIT_RECURRING.routeWithArgs,
            arguments = listOf(navArgument("wordId") {
                type = NavType.IntType
            }, navArgument("occurrenceIndex") {
                type = NavType.LongType
            })
        ) {

            val camera: String? = navController.currentBackStackEntry?.savedStateHandle["photoUri"]

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<String>("photoUri")

            EditScreen(
                navigateBack = { navController.popBackStack() },
                navigateCategories = {
                    val route = if (it == 0) {
                        AppDestination.EXPENSE_CATEGORIES.route
                    } else {
                        AppDestination.INCOME_CATEGORIES.route
                    }
                    navController.navigate(route)
                },
                navigateCamera = {
                    navController.navigate(AppDestination.CAMERA.route)
                },
                resultForCamera = camera
            )
        }

        composable(
            route = AppDestination.EXPENSE_CATEGORIES.route,
            enterTransition = { slideInHorizontally { it } }
        ) {
            ExpenseCategoriesScreen(
                navigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = AppDestination.INCOME_CATEGORIES.route,
            enterTransition = { slideInHorizontally { it } }
        ) {
            IncomeCategoriesScreen(
                navigateBack = { navController.popBackStack() },
            )
        }

    }
}