package com.firebaseapp.controlefinanceiro.ui.screens.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterScreenBody


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateCategories: (Int) -> Unit,
    navigateCamera: () -> Unit,
    resultForCamera: String?,
    viewModel: EditViewModel = viewModel(factory = ViewModelProviders.Factory)
) {

    val registerUiState by viewModel.uiState.collectAsState()



    AnimatedVisibility(viewModel.loading.value, enter = slideInHorizontally { it }) {

        Scaffold(
            modifier = modifier
                .fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    windowInsets = TopAppBarDefaults.windowInsets,
                    title = { Text("Register") },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                "Back"
                            )
                        }
                    },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding(),
            ) {



                RegisterScreenBody(
                    registerUiState = registerUiState,
                    onUpdate = viewModel::updateUiState,
                    onSave = {
                        if (viewModel.isEditingRecurrency) {
                            viewModel.updateRecurrencyWord(viewModel.recurrenceIndex ?: 0)
                            navigateBack()
                        } else {
                            viewModel.updateOneTimeWord()
                            navigateBack()
                        }
                    },
                    categoriesExpense = registerUiState.details.categoriesExpense,
                    categoriesIncome = registerUiState.details.categoriesIncome,
                    images = registerUiState.details.images,
                    navigateCategories = navigateCategories,
                    navigateCamera = navigateCamera,
                    resultForCamera = resultForCamera,
                )


            }
        }
    }

}