package com.firebaseapp.controlefinanceiro.ui.screens.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import com.firebaseapp.controlefinanceiro.data.repositories.CategoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExpenseCategoriesViewModel(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    var loading by mutableStateOf(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesUiState: StateFlow<CategoriesUiState> =
        categoryRepository.getCategoriesByTypeStream(CategoryType.Expense)
            .map { list ->
                loading = false
                CategoriesUiState(list = list)
            }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CategoriesUiState()
        )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class CategoriesUiState(
    val list: List<Category> = listOf(),
)