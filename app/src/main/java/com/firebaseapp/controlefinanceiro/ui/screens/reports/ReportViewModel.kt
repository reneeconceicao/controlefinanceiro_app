package com.firebaseapp.controlefinanceiro.ui.screens.reports

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.helpers.toMonthYear
import com.firebaseapp.controlefinanceiro.helpers.toYear
import com.firebaseapp.controlefinanceiro.recurrency.calculateWordOccurrences
import com.firebaseapp.controlefinanceiro.recurrency.calculateWordOccurrencesReport
import com.firebaseapp.controlefinanceiro.ui.screens.reports.ReportViewModel.Companion.PERIOD_TIME_FLAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class ReportViewModel(private val wordRepository: WordRepository) : ViewModel() {


    val fromDate =
        MutableStateFlow(LocalDateTime.now().toLocalDate().withDayOfMonth(1).atStartOfDay())
    val toDate = MutableStateFlow(
        LocalDateTime.now().toLocalDate()
            .withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
            .atTime(23, 59, 59, 999_999_999)
    )

    val fromDatePeriod =
        MutableStateFlow(LocalDateTime.now().withHour(0).withMinute(0).withSecond(1))

    val toDatePeriod =
        MutableStateFlow(LocalDateTime.now().withHour(23).withMinute(59).withSecond(59))

    val currentFilter = MutableStateFlow(ReportDateFilter.MONTH)

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

    val reportUiState: StateFlow<ReportUiState> =
        combine(
            fromDate,
            toDate,
            currentFilter,
            _wordsStateFlow,
            _exceptionStateFlow,
        ) { fromDate, toDate, filter, words, exceptions ->

            // view calculateWordOccurrences explanation
            val occurrences = calculateWordOccurrencesReport(
                words = words,
                exceptions = exceptions,
                startDate = fromDate,
                endDate = toDate
            )

            Log.d("TAG", "occurrences report: ${occurrences.size}")

            val periodLabel = when (currentFilter.value) {
                ReportDateFilter.MONTH -> toMonthYear(fromDate)
                ReportDateFilter.YEAR -> toYear(fromDate)
                ReportDateFilter.PERIOD -> PERIOD_TIME_FLAG
            }

            val expenses =
                occurrences.filter { it.first.type == WordType.Expense }
                    .sumOf { it.first.value }
            val income =
                occurrences.filter { it.first.type == WordType.Income }
                    .sumOf { it.first.value }
            val total = income - expenses

            loading.value = false

            ReportUiState(
                list = occurrences,
                currentMonthYear = YearMonth.from(fromDate),
                total = total,
                periodLabel = periodLabel,
                currentFilter = filter,
                fromDatePeriod = fromDate,
                toDatePeriod = toDate,
            )

        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ReportUiState()
            )


    fun changePeriod(value: Int) {
        when (currentFilter.value) {
            ReportDateFilter.PERIOD -> {
                fromDate.value = fromDatePeriod.value
                toDate.value = toDatePeriod.value
            }

            ReportDateFilter.MONTH -> {
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

            ReportDateFilter.YEAR -> {
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

    fun changeFilter(filter: ReportDateFilter) {
        currentFilter.value = filter

        when (currentFilter.value) {

            ReportDateFilter.PERIOD -> {
                fromDate.value = fromDatePeriod.value
                toDate.value = toDatePeriod.value
            }

            ReportDateFilter.MONTH -> {
                val month = YearMonth.from(selectedMonth.value)

                fromDate.value = month
                    .atDay(1)
                    .atStartOfDay()

                toDate.value = month
                    .atEndOfMonth()
                    .atTime(23, 59, 59, 999_000_000)
            }

            ReportDateFilter.YEAR -> {
                val year = selectedYear.value.year

                fromDate.value = LocalDate.of(year, 1, 1)
                    .atStartOfDay()

                toDate.value = LocalDate.of(year, 12, 31)
                    .atTime(23, 59, 59, 999_000_000)
            }


        }

    }

    fun changePeriodFrom(date: LocalDateTime) {
        fromDatePeriod.value = date
        fromDate.value = fromDatePeriod.value
    }

    fun changePeriodTo(date: LocalDateTime) {
        toDatePeriod.value = date
        toDate.value = toDatePeriod.value
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        const val PERIOD_TIME_FLAG = "all_time_flag"
    }

}

data class ReportUiState(
    val list: List<Pair<Word, Long?>> = listOf(),
    val currentMonthYear: YearMonth = YearMonth.from(LocalDateTime.now()),
    val total: BigDecimal = BigDecimal.ZERO,
    val currentFilter: ReportDateFilter = ReportDateFilter.MONTH,
    val periodLabel: String = PERIOD_TIME_FLAG,
    val fromDatePeriod: LocalDateTime = LocalDateTime.now().atStartOfDay(),
    val toDatePeriod: LocalDateTime = LocalDateTime.now().atEndOfDay()
)

enum class ReportDateFilter {
    MONTH,
    YEAR,

    PERIOD,
}

data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

fun LocalDateTime.atStartOfDay(): LocalDateTime {
    return this.toLocalDate().atStartOfDay()
}

fun LocalDateTime.atEndOfDay(): LocalDateTime {
    return this.toLocalDate().atTime(23, 59, 59, 999_999_999)
}