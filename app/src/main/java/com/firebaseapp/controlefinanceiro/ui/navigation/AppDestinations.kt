package com.firebaseapp.controlefinanceiro.ui.navigation

enum class AppDestination(
    val route: String,
    val routeWithArgs: String = ""
) {
    MAIN("main"),
    REGISTER("register"),

    CAMERA("camera"),

    EDIT(route = "edit", routeWithArgs = "edit/{wordId}"),

    EDIT_RECURRING(route = "edit_recurring", routeWithArgs = "edit_recurring/{wordId}/{occurrenceIndex}"),

    EXPENSE_CATEGORIES(route = "expense_categories", routeWithArgs = "expense_categories/{categoryType}"),

    INCOME_CATEGORIES(route = "income_categories", routeWithArgs = "income_categories/{categoryType}")
}