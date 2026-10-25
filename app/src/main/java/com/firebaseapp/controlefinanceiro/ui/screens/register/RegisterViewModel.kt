package com.firebaseapp.controlefinanceiro.ui.screens.register

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordException
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.data.repositories.CategoryRepository
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.helpers.toBigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import kotlin.Int

class RegisterViewModel(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var registerState = MutableStateFlow(RegisterDetails())

    val lastPhotoUri = savedStateHandle
        .getStateFlow<String?>("photoUri", null)


    init {
        Log.d("TAG", "VIEW MODEL STATE: ${lastPhotoUri.value}")
    }
    var uiState: StateFlow<RegisterUiState> = combine(
        registerState,
        categoryRepository.getCategoriesByTypeStream(CategoryType.Expense),
        categoryRepository.getCategoriesByTypeStream(CategoryType.Income)
    ) { state, categoriesExpense, categoriesIncome ->

        RegisterUiState(details = state.copy(categoriesExpense = categoriesExpense, categoriesIncome = categoriesIncome))

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        RegisterUiState()
    )

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

    fun updateUiState(details: RegisterDetails) {
        this.registerState.value = details
        Log.d("TAG", "updateUiState: ${details.images}")
    }

    fun insertWord(details: RegisterDetails) {
        viewModelScope.launch {
            var groupRecurringId = ""
            if (details.isRecurring) {
                groupRecurringId = UUID.randomUUID().toString()
            }
            wordRepository.insertWord(details.toWord().copy(recurringGroup = groupRecurringId))
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
    var date: LocalDateTime = LocalDateTime.now(),
    var notes: String = "",
    var currentCategoryId: Int = 0,
    var categoryName: String = "",
    var categoryIcon: String = "",
    var categoriesExpense: List<Category> = emptyList(),
    var categoriesIncome: List<Category> = emptyList(),
    var images: List<String> = emptyList(),
    var isRecurring: Boolean = false,
    var recurringGroupId: String = "",
    var endDate: LocalDateTime? = null,
    var isEditRecurring: Boolean = false
)

fun RegisterDetails.toWord(): Word {
    return Word(
        id = id,
        date = date,
        type = if (selectedOption == 0) WordType.Expense else WordType.Income,
        value = toBigDecimal(price),
        categoryId = currentCategoryId,
        categoryName = categoryName,
        categoryIcon = categoryIcon,
        notes = notes,
        images = images,
        isRecurring = isRecurring,
        recurringGroup = recurringGroupId,
        endDate = endDate,
    )
}

fun RegisterDetails.toNewWord(): Word {
    return Word(
        date = date,
        type = if (selectedOption == 0) WordType.Expense else WordType.Income,
        value = toBigDecimal(price),
        categoryId = currentCategoryId,
        categoryName = categoryName,
        categoryIcon = categoryIcon,
        notes = notes,
        images = images,
        isRecurring = isRecurring,
        recurringGroup = recurringGroupId,
        endDate = endDate,
    )
}

fun RegisterDetails.toWordException(
    wordId: Int,
    occurrenceIndex: Long,
    recurringGroupId: String
): WordException {
    return WordException(
        wordId = wordId,
        occurrenceIndex = occurrenceIndex,
        date = date,
        type = if (selectedOption == 0) WordType.Expense else WordType.Income,
        value = toBigDecimal(price),
        categoryId = currentCategoryId,
        categoryName = categoryName,
        categoryIcon = categoryIcon,
        notes = notes,
        images = images,
        isDeleted = false,
        recurringGroup = recurringGroupId
    )
}

fun Word.toDetails(): RegisterDetails {
    return RegisterDetails(
        id = id,
        selectedOption = if (type == WordType.Expense) 0 else 1,
        price = value.toString(),
        date = date,
        notes = notes,
        currentCategoryId = categoryId,
        categoryIcon = categoryIcon,
        categoryName = categoryName,
        images = images.toMutableList(),
        isRecurring = isRecurring,
        recurringGroupId = recurringGroup
    )
}

fun WordException.toDetails(): RegisterDetails {
    return RegisterDetails(
        id = id,
        selectedOption = if (type == WordType.Expense) 0 else 1,
        price = value.toString(),
        date = date,
        notes = notes,
        images = images.toMutableList(),
        currentCategoryId = categoryId,
        categoryIcon = categoryIcon,
        categoryName = categoryName,
    )
}

fun WordException.toWord(): Word {
    return Word(
        date = date,
        type = type,
        value = value,
        categoryId = categoryId,
        categoryName = categoryName,
        categoryIcon = categoryIcon,
        notes = notes,
        images = images,
        isRecurring = false,
        recurringGroup = recurringGroup,
        endDate = null,
    )
}