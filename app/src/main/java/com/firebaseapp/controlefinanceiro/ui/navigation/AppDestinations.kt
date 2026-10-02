package com.firebaseapp.controlefinanceiro.ui.navigation

enum class AppDestination(
    val route: String,
    val routeWithArgs: String = ""
) {
    MAIN("main"),
    REGISTER("register"),

    EDIT(route = "edit", routeWithArgs = "edit/{wordId}"),

    EXPENSE_CATEGORIES(route = "expense_categories", routeWithArgs = "expense_categories/{categoryType}"),

    INCOME_CATEGORIES(route = "income_categories", routeWithArgs = "income_categories/{categoryType}")
}