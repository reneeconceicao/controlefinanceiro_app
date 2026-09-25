package com.firebaseapp.controlefinanceiro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    HOME("home", "Home", Icons.Default.Home, "Home"),
    BUDGET("budgets", "Budgets", Icons.Default.Build, "Budgets"),
    REPORT("report", "Reporting", Icons.Default.Refresh, "Reporting"),

    SETTINGS("settings", "Settings", Icons.Default.Settings, "Settings")
}