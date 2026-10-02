package com.firebaseapp.controlefinanceiro.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.firebaseapp.controlefinanceiro.MoneyManagerApplication
import com.firebaseapp.controlefinanceiro.ui.screens.budgets.BudgetViewModel
import com.firebaseapp.controlefinanceiro.ui.screens.categories.ExpenseCategoriesViewModel
import com.firebaseapp.controlefinanceiro.ui.screens.categories.IncomeCategoriesViewModel
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeViewModel
import com.firebaseapp.controlefinanceiro.ui.screens.edit.EditViewModel
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterViewModel


object ViewModelProviders {
    val Factory = viewModelFactory {

        initializer {
            HomeViewModel(getApplication().wordRepository)
        }

        initializer {
            RegisterViewModel(getApplication().wordRepository, getApplication().categoryRepository)
        }

        initializer {
            EditViewModel(getApplication().wordRepository, getApplication().categoryRepository, createSavedStateHandle())
        }

        initializer {
            ExpenseCategoriesViewModel(getApplication().categoryRepository)
        }

        initializer {
            IncomeCategoriesViewModel(getApplication().categoryRepository)
        }

        initializer {
            BudgetViewModel(getApplication().budgetRepository, getApplication().wordRepository)
        }
    }
}

fun CreationExtras.getApplication(): MoneyManagerApplication {
    return (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoneyManagerApplication)
}

