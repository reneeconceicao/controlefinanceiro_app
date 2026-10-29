package com.firebaseapp.controlefinanceiro.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DetailsViewModel(private val wordRepository: WordRepository) : ViewModel() {

    var currentId = MutableStateFlow<Long?>(null)

    fun updateId(id: Int) {
        currentId.update {
            id.toLong()
        }
    }

    val currentWordStateFlow = combine(currentId) { (id) ->
        id?.let {
            return@combine wordRepository.getWordAsync(it.toInt())
        }
        return@combine null
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = null
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

