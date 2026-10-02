package com.firebaseapp.controlefinanceiro.ui.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.helpers.firstDayOfCurrentMonth
import com.firebaseapp.controlefinanceiro.helpers.lastDayOfCurrentMonth
import com.firebaseapp.controlefinanceiro.helpers.setEndOfDay
import com.firebaseapp.controlefinanceiro.helpers.setStartOfDay
import com.firebaseapp.controlefinanceiro.helpers.toMonthYear
import com.firebaseapp.controlefinanceiro.helpers.toYear
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeViewModel.Companion.ALL_TIME_FLAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

class HomeViewModel(private val wordRepository: WordRepository) : ViewModel() {


    val fromDate = MutableStateFlow(Date(Long.MIN_VALUE))
    val toDate = MutableStateFlow(Date(Long.MAX_VALUE))

    val currentFilter = MutableStateFlow(DateFilter.ALL)

    private val selectedMonth = MutableStateFlow(Date())

    private val selectedYear = MutableStateFlow(Date())

    val loading = mutableStateOf(true)


    @OptIn(ExperimentalCoroutinesApi::class)
    val homeUiState: StateFlow<HomeUiState> =
        combine(fromDate, toDate, currentFilter) { fromDate, toDate, filter -> Triple(fromDate, toDate, filter) }
            .flatMapLatest { (fromDate, toDate, filter) ->

                wordRepository.getWordsByDateStream(fromDate, toDate).map { list ->
                    val periodLabel = when (currentFilter.value) {
                        DateFilter.ALL -> ALL_TIME_FLAG
                        DateFilter.MONTH -> toMonthYear(fromDate)
                        DateFilter.YEAR -> toYear(fromDate)
                    }
                    val expenses = list.filter { it.type == WordType.Expense }.sumOf { it.value }
                    val income = list.filter { it.type == WordType.Income }.sumOf { it.value }
                    val total = income - expenses

                    loading.value = false

                    HomeUiState(
                        list = list,
                        total = total,
                        periodLabel = periodLabel,
                        currentFilter = filter
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )



    fun changePeriod(value: Int) {
        when (currentFilter.value) {
            DateFilter.ALL -> {
                fromDate.value = Date(Long.MIN_VALUE)
                toDate.value = Date(Long.MAX_VALUE)
            }

            DateFilter.MONTH -> {
                val calendar = Calendar.getInstance()

                calendar.time = selectedMonth.value
                calendar.add(Calendar.MONTH, value)
                selectedMonth.value = calendar.time

                calendar.set(Calendar.DAY_OF_MONTH, 1)
                setStartOfDay(calendar)
                fromDate.value = calendar.time


                val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, lastDay)
                setEndOfDay(calendar)
                toDate.value = calendar.time

            }

            DateFilter.YEAR -> {
                val calendar = Calendar.getInstance()

                calendar.time = selectedYear.value
                calendar.add(Calendar.YEAR, value)
                selectedYear.value = calendar.time

                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                setStartOfDay(calendar)
                fromDate.value = calendar.time


                calendar.set(Calendar.MONTH, Calendar.DECEMBER)
                calendar.set(Calendar.DAY_OF_MONTH, 31)
                setEndOfDay(calendar)
                toDate.value = calendar.time

            }
        }


    }

    fun changeFilter(filter: DateFilter) {
        currentFilter.value = filter

        val calendar = Calendar.getInstance()

        when (currentFilter.value) {

            DateFilter.ALL -> {
                fromDate.value = Date(Long.MIN_VALUE)
                toDate.value = Date(Long.MAX_VALUE)
            }

            DateFilter.MONTH -> {
                calendar.time = selectedMonth.value

                calendar.set(Calendar.DAY_OF_MONTH, 1)
                setStartOfDay(calendar)
                fromDate.value = calendar.time

                val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, lastDay)
                setEndOfDay(calendar)
                toDate.value = calendar.time
            }

            DateFilter.YEAR -> {
                calendar.time = selectedYear.value

                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                setStartOfDay(calendar)
                fromDate.value = calendar.time

                calendar.set(Calendar.MONTH, Calendar.DECEMBER)
                calendar.set(Calendar.DAY_OF_MONTH, 31)
                setEndOfDay(calendar)
                toDate.value = calendar.time
            }


        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        const val ALL_TIME_FLAG = "all_time_flag"
    }

}

data class HomeUiState(
    val list: List<Word> = listOf(),
    val total: BigDecimal = BigDecimal.ZERO,
    val currentFilter: DateFilter = DateFilter.ALL,
    val periodLabel: String = ALL_TIME_FLAG
)

enum class DateFilter {
    ALL,
    MONTH,
    YEAR
}