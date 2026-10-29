package com.firebaseapp.controlefinanceiro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.firebaseapp.controlefinanceiro.R

enum class MenuDestination(
    val route: String,
    val label: Int,
    val icon: ImageVector,
    val contentDescription: Int
) {


    HOME("home", R.string.home, Icons.Default.Home, R.string.home),
    REPORT("report", R.string.reporting, Icons.Outlined.Info, R.string.reporting),

    BUDGET("budgets", R.string.budgets, Icons.Default.TrackChanges, R.string.budgets),

    SETTINGS("settings", R.string.settings, Icons.Default.Settings, R.string.settings)
}