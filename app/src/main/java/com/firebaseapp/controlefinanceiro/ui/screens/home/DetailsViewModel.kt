package com.firebaseapp.controlefinanceiro.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import com.firebaseapp.controlefinanceiro.ui.screens.register.toOriginalWord
import com.firebaseapp.controlefinanceiro.ui.screens.register.toWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DetailsViewModel(private val wordRepository: WordRepository) : ViewModel() {

    var currentId = MutableStateFlow<Long?>(null)

    var currentIndex = MutableStateFlow<Long?>(null)

    fun updateWordIdIndex(id: Int, index: Long?) {
        currentId.update {
            id.toLong()
        }
        index?.let {
            currentIndex.update {
                index
            }
        }
    }

    fun clearWordValue() {
        currentId.value = null
    }

    val currentWordStateFlow = combine(currentId, currentIndex) { (id, index) ->
        id?.let {

            val word =  wordRepository.getWordAsync(it.toInt())
            if (word?.isRecurring == false) {
                return@combine word
            }

            word?.let { word ->
                val exceptions = wordRepository.getWordExceptionsByWordAsync(word.id)
                val exception = exceptions.find { exception ->
                    exception.wordId == word.id && exception.occurrenceIndex == index
                }
                exception?.let {
                    return@combine exception.toOriginalWord(word.isRecurring, word.endDate)
                }

                return@combine word
            }


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

