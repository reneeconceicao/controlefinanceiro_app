package com.firebaseapp.controlefinanceiro.ui.screens.edit

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import com.firebaseapp.controlefinanceiro.data.repositories.CategoryRepository
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.recurrency.findExceptions
import com.firebaseapp.controlefinanceiro.recurrency.occurrenceIndex
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterDetails
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterUiState
import com.firebaseapp.controlefinanceiro.ui.screens.register.toDetails
import com.firebaseapp.controlefinanceiro.ui.screens.register.toNewWord
import com.firebaseapp.controlefinanceiro.ui.screens.register.toWord
import com.firebaseapp.controlefinanceiro.ui.screens.register.toWordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth
import java.util.UUID
import kotlin.Int

class EditViewModel(
    private val wordRepository: WordRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val wordId: Int = checkNotNull(savedStateHandle["wordId"])

    val recurrenceIndex: Long? = savedStateHandle["occurrenceIndex"]


    init {


        viewModelScope.launch {


            val word = wordRepository.getWordAsync(wordId)

            word?.let {

                if (word.isRecurring) {

                    isEditingRecurrency = true
                    recurringGroupId.value = word.recurringGroup ?: ""

                    val exceptions = wordRepository.getWordExceptionsByWordAsync(wordId)

                    word.findExceptions(
                        recurrenceIndex ?: 0,
                        exceptions = exceptions,
                        onOccurrenceFound = { word ->
                            val details = word.toDetails()
                            registerState.value = details.copy(isEditRecurring = true)
                            originalDate.value = word.date
                            originalImages.value = word.images
                        },
                        onExceptionFound = { exception ->
                            val details = exception.toDetails()
                            registerState.value = details.copy(isEditRecurring = true)
                            originalDate.value = exception.date
                            originalImages.value = exception.images
                        })

                } else {

                    isEditingRecurrency = false

                    val details = word.toDetails()
                    val isReplaceWord =
                        !details.toWord().isRecurring && details.toWord().recurringGroup != ""
                    registerState.value = details.copy(isEditRecurring = isReplaceWord)
                    originalImages.value = word.images
                }
            }


            //loading = false
        }
    }

    val originalDate = MutableStateFlow<LocalDateTime?>(null)

    val recurringGroupId = MutableStateFlow("")

    var registerState = MutableStateFlow(RegisterDetails())


    var loading = mutableStateOf(false)

    var originalImages = mutableStateOf(listOf<String>())

    var uiState: StateFlow<RegisterUiState> = combine(
        registerState,
        categoryRepository.getCategoriesByTypeStream(CategoryType.Expense),
        categoryRepository.getCategoriesByTypeStream(CategoryType.Income)
    ) { state, categoriesExpense, categoriesIncome ->

        if (state.id != 0 && categoriesExpense.isNotEmpty() && categoriesIncome.isNotEmpty()) {
            loading.value = true
        }

        var currentCategoryId = state.currentCategoryId
        var currentCategoryName = state.categoryName
        var currentCategoryIcon = state.categoryIcon

        val categories = categoriesExpense + categoriesIncome
        if (!categories.map { it.id }.contains(state.currentCategoryId)) {
            currentCategoryId = 0
            currentCategoryName = ""
            currentCategoryIcon = ""
        }

        RegisterUiState(
            details = state.copy(
                currentCategoryId = currentCategoryId,
                categoryName = currentCategoryName,
                categoryIcon = currentCategoryIcon,
                categoriesExpense = categoriesExpense,
                categoriesIncome = categoriesIncome
            )
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        RegisterUiState()
    )

    var isEditingRecurrency by mutableStateOf(false)

    fun updateUiState(details: RegisterDetails) {
        registerState.value = details
    }

    fun updateOneTimeWord() {
        viewModelScope.launch {
            if (registerState.value.isRecurring) {
                //transform one time word to recurrency
                wordRepository.updateWord(
                    registerState.value.toWord()
                        .copy(isRecurring = true, recurringGroup = UUID.randomUUID().toString()),
                    originalImages.value
                )
                return@launch
            }
            wordRepository.updateWord(registerState.value.toWord(), originalImages.value)
        }
    }

    fun updateRecurrencyWord(occurrenceIndex: Long) {
        viewModelScope.launch {
            var exception =
                registerState.value.toWordException(
                    wordId = wordId,
                    occurrenceIndex = occurrenceIndex,
                    recurringGroupId = recurringGroupId.value.ifEmpty {
                        UUID.randomUUID().toString()
                    }
                )

            originalDate.value?.let { originalDate ->
                if (YearMonth.from(originalDate) != YearMonth.from(exception.date)) {
                    exception = exception.copy(isDeleted = true)
                    val replaceWord = exception.toWord()
                    wordRepository.insertWord(replaceWord.copy(recurringGroup = recurringGroupId.value))
                }
            }

            wordRepository.insertWordException(exception)

        }
    }

    fun updateThisWordAndNext(occurrenceIndex: Long) {
        viewModelScope.launch(Dispatchers.IO) {

            val originalWord = wordRepository.getWordAsync(wordId)
            originalWord?.let {

                val newWord = registerState.value.toNewWord()

                val futureWords =
                    wordRepository.wordsByRecurringGroup(originalWord.recurringGroup)
                        .filter { newWord.date <= it.date }

                futureWords.forEach {
                    if (it.isRecurring) {
                        wordRepository.deleteWord(it)
                        Log.d("TAG", "deleted: $it")
                    }
                }

                val newWordId = wordRepository.insertWord(newWord).toInt()

                val futureExceptions =
                    wordRepository.exceptionsByRecurringGroup(originalWord.recurringGroup)
                        .filter { newWord.date <= it.date }

                futureExceptions.forEach {
                    if (!it.isDeleted) {
                        wordRepository.deleteException(it)
                    } else {
                        val oldBaseDate = originalWord.date.plusMonths(it.occurrenceIndex)
                        val index =
                            occurrenceIndex(newWord.date, oldBaseDate, newWord.interval) ?: 0
                        val exception =
                            it.copy(wordId = newWordId, occurrenceIndex = index, isDeleted = true)
                        wordRepository.deleteException(it)
                        wordRepository.insertWordException(exception)
                    }
                }

                val updateOriginalWord = originalWord.copy(
                    endDate = registerState.value.date
                        .withDayOfMonth(1)
                        .minusSeconds(1)
                )

                wordRepository.updateWord(updateOriginalWord, originalImages.value)

            }
        }
    }


    fun isDifferentMonths(date1: LocalDateTime?, date2: LocalDateTime?): Boolean {
        return YearMonth.from(date1) != YearMonth.from(
            date2
        )
    }

}
