package com.firebaseapp.controlefinanceiro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val route: String,
    val routeWithArgs: String = ""
) {
    MAIN("main"),
    REGISTER("register"),

    EDIT(route = "edit", routeWithArgs = "edit/{wordId}")
}