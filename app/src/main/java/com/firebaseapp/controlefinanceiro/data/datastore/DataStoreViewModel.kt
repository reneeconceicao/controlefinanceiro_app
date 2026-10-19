package com.firebaseapp.controlefinanceiro.data.datastore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DataStoreViewModel(private val repository: PreferencesDataStore) : ViewModel() {

    var loading by mutableStateOf(true)

    val readCounter: StateFlow<Int> =
        repository.counterFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0)

    fun addCounter() {
        viewModelScope.launch {
            repository.incrementCounter()
        }
    }

    val isHapticEnabled: StateFlow<Boolean> =
        repository.hapticEnabledFlow().onEach { loading = false }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = true)

    fun updateHaptic(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateHaptic(enabled)
        }
    }
}