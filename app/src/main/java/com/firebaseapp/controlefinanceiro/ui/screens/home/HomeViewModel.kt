package com.firebaseapp.controlefinanceiro.ui.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.helpers.firstDayOfCurrentMonth
import com.firebaseapp.controlefinanceiro.helpers.lastDayOfCurrentMonth
import com.firebaseapp.controlefinanceiro.helpers.setEndOfDay
import com.firebaseapp.controlefinanceiro.helpers.setStartOfDay
import com.firebaseapp.controlefinanceiro.helpers.toMonthYear
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

class HomeViewModel(private val wordRepository: WordRepository) : ViewModel() {


    val fromDate = MutableStateFlow(firstDayOfCurrentMonth())
    val toDate = MutableStateFlow(lastDayOfCurrentMonth())

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeUiState: StateFlow<HomeUiState> =
        combine(fromDate, toDate) { fromDate, toDate -> fromDate to toDate }
            .flatMapLatest { (fromDate, toDate) ->
                wordRepository.getWordsByDateStream(fromDate, toDate).map { list ->
                    val periodLabel = toMonthYear(fromDate)
                    val total = BigDecimal(list.sumOf { it.hours })
                    HomeUiState(list = list, total = total, periodLabel = periodLabel)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    fun changePeriod(value: Int) {
        val calendar = Calendar.getInstance()


        calendar.time = fromDate.value
        calendar.add(Calendar.MONTH, value)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        setStartOfDay(calendar)
        fromDate.value = calendar.time


        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDay)
        setEndOfDay(calendar)
        toDate.value = calendar.time


    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class HomeUiState(
    val list: List<Word> = listOf(),
    val total: BigDecimal = BigDecimal.ZERO,
    val periodLabel: String = toMonthYear(Date())
)