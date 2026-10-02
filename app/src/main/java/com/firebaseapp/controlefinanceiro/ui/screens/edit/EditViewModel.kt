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


            val categories =
                categoryRepository.getAllCategoryStream("%%")
                    .stateIn(viewModelScope).value

            categoriesExpense = categories.filter { it.categoryType == CategoryType.Expense }
            categoriesIncome = categories.filter { it.categoryType == CategoryType.Income }

            loading = false
        }
    }

    var uiState by mutableStateOf(RegisterUiState())

    var categoriesExpense by mutableStateOf(listOf<Category>())

    var categoriesIncome by mutableStateOf(listOf<Category>())

    var loading by mutableStateOf(true)

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
