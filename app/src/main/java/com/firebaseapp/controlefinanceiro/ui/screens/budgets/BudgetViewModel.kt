package com.firebaseapp.controlefinanceiro.ui.screens.budgets

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Budget
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.data.repositories.BudgetRepository
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val wordRepository: WordRepository
) : ViewModel() {


    val fromDate = MutableStateFlow(firstDayOfCurrentMonth())
    val toDate = MutableStateFlow(lastDayOfCurrentMonth())

    private val selectedMonth = MutableStateFlow(Date())

    val loading = mutableStateOf(true)


    @OptIn(ExperimentalCoroutinesApi::class)
    val budgetUiState: StateFlow<BudgetUiState> =
        combine(fromDate, toDate) { fromDate, toDate -> fromDate to toDate }
            .flatMapLatest { (fromDate, toDate) ->

                val periodLabel = toMonthYear(fromDate)

                val words = wordRepository.getWordsByDateStream(fromDate, toDate).stateIn(viewModelScope).value

                budgetRepository.getAllBudgetsStream().map { list ->

                    val newList = mutableListOf<Budget>()

                    list.forEach { budget ->
                        val wordsByCategory = words.filter { it.categoryId == budget.categoryId}
                        val totalValue = wordsByCategory.sumOf { it.value }
                        newList.add(budget.copy(currentValue = totalValue))
                    }


                    loading.value = false

                    BudgetUiState(
                        list = newList,
                        periodLabel = periodLabel,
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BudgetUiState()
            )


    fun changePeriod(value: Int) {
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


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }






}

data class BudgetUiState(
    val list: List<Budget> = listOf(),
    val periodLabel: String = ALL_TIME_FLAG
)