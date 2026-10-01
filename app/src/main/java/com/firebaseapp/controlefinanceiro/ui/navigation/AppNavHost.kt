package com.firebaseapp.controlefinanceiro.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.firebaseapp.controlefinanceiro.ui.screens.main.EditScreen
import com.firebaseapp.controlefinanceiro.ui.screens.main.MainScreen
import com.firebaseapp.controlefinanceiro.ui.screens.main.RegisterScreen


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
                    navController.navigate("${AppDestination.EDIT.route}/${it}")
                })
        }

        composable(AppDestination.REGISTER.route, enterTransition = { slideInHorizontally { it } }) {
            RegisterScreen(navigateBack = { navController.popBackStack() })
        }

        composable(
            route = AppDestination.EDIT.routeWithArgs,
            arguments = listOf(navArgument("wordId") {
                type = NavType.IntType
            })
        ) {
            EditScreen(
                navigateBack = { navController.popBackStack() },
            )
        }

    }
}