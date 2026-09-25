package com.firebaseapp.controlefinanceiro.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
            MainScreen(navigateToRegister = { navController.navigate(AppDestination.REGISTER.route) })
        }

        composable(AppDestination.REGISTER.route) {
            RegisterScreen(navigateBack = { navController.popBackStack() })
        }

    }
}