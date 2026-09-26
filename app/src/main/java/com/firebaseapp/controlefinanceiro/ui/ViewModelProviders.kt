package com.firebaseapp.controlefinanceiro.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.firebaseapp.controlefinanceiro.MoneyManagerApplication
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeViewModel
import com.firebaseapp.controlefinanceiro.ui.screens.main.RegisterViewModel


object ViewModelProviders {
    val Factory = viewModelFactory {

        initializer {
            HomeViewModel(getApplication().wordRepository)
        }

        initializer {
            RegisterViewModel(getApplication().wordRepository)
        }
    }
}

fun CreationExtras.getApplication(): MoneyManagerApplication {
    return (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoneyManagerApplication)
}

