package com.firebaseapp.controlefinanceiro.ui.screens.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import com.firebaseapp.controlefinanceiro.data.repositories.CategoryRepository
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterDetails
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterUiState
import com.firebaseapp.controlefinanceiro.ui.screens.register.toDetails
import com.firebaseapp.controlefinanceiro.ui.screens.register.toWord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditViewModel(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val wordId: Int = checkNotNull(savedStateHandle["wordId"])

    init {

        viewModelScope.launch {
            val word = wordRepository.getWordStream(wordId).stateIn(viewModelScope).value

            word?.let {
                uiState = RegisterUiState(details = it.toDetails())
            }

            //loading = false
        }
    }

    var uiState by mutableStateOf(RegisterUiState())

    val categoriesExpense: StateFlow<List<Category>> = categoryRepository.getCategoriesByTypeStream(
        CategoryType.Expense
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyList()
    )

    var categoriesIncome: StateFlow<List<Category>> = categoryRepository.getCategoriesByTypeStream(
        CategoryType.Income
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyList()
    )

    var isCategoriesLoaded: StateFlow<Boolean> =
        combine(categoriesIncome, categoriesExpense) { categoriesExpense, categoryIncome ->
            categoriesExpense.isNotEmpty() && categoryIncome.isNotEmpty()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    fun updateUiState(details: RegisterDetails) {
        this.uiState = RegisterUiState(
            details = details,
        )
    }

    fun updateWord() {
        viewModelScope.launch {
            wordRepository.updateWord(uiState.details.toWord())
        }
    }

}
