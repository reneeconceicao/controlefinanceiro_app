package com.firebaseapp.controlefinanceiro.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.helpers.calculateWordOccurrences
import com.firebaseapp.controlefinanceiro.helpers.findExceptions
import com.firebaseapp.controlefinanceiro.helpers.toMonthYear
import com.firebaseapp.controlefinanceiro.helpers.toYear
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeViewModel.Companion.ALL_TIME_FLAG
import com.firebaseapp.controlefinanceiro.ui.screens.register.toWord
import com.firebaseapp.controlefinanceiro.ui.screens.register.toWordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class HomeViewModel(private val wordRepository: WordRepository) : ViewModel() {


    val fromDate =
        MutableStateFlow(LocalDateTime.now().toLocalDate().withDayOfMonth(1).atStartOfDay())
    val toDate = MutableStateFlow(
        LocalDateTime.now().toLocalDate()
            .withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
            .atTime(23, 59, 59, 999_999_999)
    )

    val currentFilter = MutableStateFlow(DateFilter.MONTH)

    private val selectedMonth = MutableStateFlow(LocalDateTime.now())

    private val selectedYear = MutableStateFlow(LocalDateTime.now())

    val loading = mutableStateOf(true)

    private val _wordsStateFlow = wordRepository.getAllWordsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    private val _exceptionStateFlow = wordRepository.getAllWordsExceptionsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val homeUiState: StateFlow<HomeUiState> =
        combine(
            fromDate,
            toDate,
            currentFilter,
            _wordsStateFlow,
            _exceptionStateFlow,
        ) { fromDate, toDate, filter, words, exceptions ->

            // view calculateWordOccurrences explanation
            val occurrences = calculateWordOccurrences(
                words = words,
                exceptions = exceptions,
                startDate = fromDate,
                endDate = toDate
            )

            val periodLabel = when (currentFilter.value) {
                DateFilter.ALL -> ALL_TIME_FLAG
                DateFilter.MONTH -> toMonthYear(fromDate)
                DateFilter.YEAR -> toYear(fromDate)
            }

            val expenses =
                occurrences.filter { it.first.type == WordType.Expense }
                    .sumOf { it.first.value }
            val income =
                occurrences.filter { it.first.type == WordType.Income }
                    .sumOf { it.first.value }
            val total = income - expenses

            loading.value = false

            HomeUiState(
                list = occurrences,
                currentMonthYear = YearMonth.from(fromDate),
                total = total,
                periodLabel = periodLabel,
                currentFilter = filter
            )

        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )


    fun changePeriod(value: Int) {
        when (currentFilter.value) {
            DateFilter.ALL -> {
                fromDate.value = LocalDateTime.MIN
                toDate.value = LocalDateTime.MAX
            }

            DateFilter.MONTH -> {
                val month = selectedMonth.value.plusMonths(value.toLong())
                selectedMonth.value = month

                fromDate.value = month
                    .withDayOfMonth(1)
                    .toLocalDate()
                    .atStartOfDay()

                toDate.value = month
                    .toLocalDate()
                    .withDayOfMonth(month.toLocalDate().lengthOfMonth())
                    .atTime(23, 59, 59, 999_000_000)
            }

            DateFilter.YEAR -> {
                val year = selectedYear.value.plusYears(value.toLong())
                selectedYear.value = year

                fromDate.value = year
                    .withMonth(1)
                    .withDayOfMonth(1)
                    .toLocalDate()
                    .atStartOfDay()

                toDate.value = year
                    .withMonth(12)
                    .withDayOfMonth(31)
                    .toLocalDate()
                    .atTime(23, 59, 59, 999_000_000)
            }
        }


    }

    fun changeFilter(filter: DateFilter) {
        currentFilter.value = filter

        when (currentFilter.value) {

            DateFilter.ALL -> {
                fromDate.value = LocalDateTime.MIN
                toDate.value = LocalDateTime.MAX
            }

            DateFilter.MONTH -> {
                val month = YearMonth.from(selectedMonth.value)

                fromDate.value = month
                    .atDay(1)
                    .atStartOfDay()

                toDate.value = month
                    .atEndOfMonth()
                    .atTime(23, 59, 59, 999_000_000)
            }

            DateFilter.YEAR -> {
                val year = selectedYear.value.year

                fromDate.value = LocalDate.of(year, 1, 1)
                    .atStartOfDay()

                toDate.value = LocalDate.of(year, 12, 31)
                    .atTime(23, 59, 59, 999_000_000)
            }


        }

    }

    fun deleteWord(word: Word, recurrenceIndex: Long?, deleteNext: Boolean, deleteAll: Boolean = false) {

        viewModelScope.launch(Dispatchers.IO) {

            if (deleteAll) {
                deleteAllOccurrences(word)
                wordRepository.deleteWord(word)
                return@launch
            }

            if (word.isRecurring) {
                val exceptions = wordRepository.getWordExceptionsByWordAsync(word.id)

                word.findExceptions(
                    recurrenceIndex ?: 0,
                    exceptions = exceptions,
                    onOccurrenceFound = { foundWord ->
                        viewModelScope.launch(Dispatchers.IO) {
                            if (deleteNext) {
                                val newWord = word.copy(endDate = foundWord.date.minusSeconds(1))
                                wordRepository.updateWord(newWord)
                                deleteFutureOccurrences(foundWord)
                            } else {
                                val exception = foundWord.toWordException(
                                    occurrenceIndex = recurrenceIndex ?: 0,
                                    isDeleted = true
                                )
                                wordRepository.insertWordException(exception)
                            }
                        }
                    },
                    onExceptionFound = { exception ->
                        viewModelScope.launch(Dispatchers.IO) {
                            if (deleteNext) {
                                val newWord = word.copy(endDate = exception.date.minusSeconds(1))
                                wordRepository.updateWord(newWord)
                                deleteFutureOccurrences(exception.toWord())
                            } else {
                                wordRepository.deleteException(exception)
                            }
                        }
                    })

            } else {
                wordRepository.deleteWord(word)
            }
        }
    }

    private fun deleteFutureOccurrences(word: Word) {

        viewModelScope.launch(Dispatchers.IO) {

            val futureWords =
                wordRepository.wordsByRecurringGroup(word.recurringGroup)
                    .filter { word.date <= it.date }

            futureWords.forEach {
                if (it.isRecurring) {
                    wordRepository.deleteWord(it)
                }
            }

            val futureExceptions =
                wordRepository.exceptionsByRecurringGroup(word.recurringGroup)
                    .filter { word.date <= it.date }

            futureExceptions.forEach {
                wordRepository.deleteException(it)
            }

        }
    }

    private fun deleteAllOccurrences(word: Word) {

        viewModelScope.launch(Dispatchers.IO) {

            val allWords =
                wordRepository.wordsByRecurringGroup(word.recurringGroup)

            allWords.forEach {
                if (it.isRecurring) {
                    wordRepository.deleteWord(it)
                }
            }

            val allExceptions =
                wordRepository.exceptionsByRecurringGroup(word.recurringGroup)

            allExceptions.forEach {
                wordRepository.deleteException(it)
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        const val ALL_TIME_FLAG = "all_time_flag"
    }

}

data class HomeUiState(
    val list: List<Pair<Word, Long?>> = listOf(),
    val currentMonthYear: YearMonth = YearMonth.from(LocalDateTime.now()),
    val total: BigDecimal = BigDecimal.ZERO,
    val currentFilter: DateFilter = DateFilter.ALL,
    val periodLabel: String = ALL_TIME_FLAG
)

enum class DateFilter {
    ALL,
    MONTH,
    YEAR
}

data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

