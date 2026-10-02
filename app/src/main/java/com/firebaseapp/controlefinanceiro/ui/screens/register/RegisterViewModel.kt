package com.firebaseapp.controlefinanceiro.ui.screens.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.data.repositories.CategoryRepository
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.helpers.toBigDecimal
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import kotlin.Int

class RegisterViewModel(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {


    init {

        viewModelScope.launch {
            val categories =
                categoryRepository.getAllCategoryStream("%%")
                    .stateIn(viewModelScope).value

            categoriesExpense = categories.filter { it.categoryType == CategoryType.Expense }
            categoriesIncome = categories.filter { it.categoryType == CategoryType.Income }
        }
    }

    var uiState by mutableStateOf(RegisterUiState())

    var categoriesExpense by mutableStateOf(listOf<Category>())

    var categoriesIncome by mutableStateOf(listOf<Category>())

    fun updateUiState(details: RegisterDetails) {
        this.uiState = RegisterUiState(
            details = details,
        )
    }

    fun insertWord(details: RegisterDetails) {
        viewModelScope.launch {
            wordRepository.insertWord(details.toWord())
        }
    }

}

data class RegisterUiState(

    var details: RegisterDetails = RegisterDetails(),

    )

data class RegisterDetails(
    var id: Int = 0,
    var selectedOption: Int = 0,
    var price: String = "0",
    var date: Date = Date(System.currentTimeMillis()),
    var notes: String = "",
    var currentCategoryId: Int = 0,
    var categoryName: String = "",
)

fun RegisterDetails.toWord(): Word {
    return Word(
        id = id,
        date = date,
        type = if (selectedOption == 0) WordType.Expense else WordType.Income,
        value = toBigDecimal(price),
        categoryId = currentCategoryId,
        categoryName = categoryName,
        notes = notes
    )
}

fun Word.toDetails(): RegisterDetails {
    return RegisterDetails (
        id = id,
        selectedOption = if (type == WordType.Expense) 0 else 1,
        price = value.toString(),
        date = date,
        notes = notes ,
        currentCategoryId = categoryId,
        categoryName = categoryName,
    )
}