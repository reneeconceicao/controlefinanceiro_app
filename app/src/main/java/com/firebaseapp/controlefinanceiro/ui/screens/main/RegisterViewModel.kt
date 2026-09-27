package com.firebaseapp.controlefinanceiro.ui.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import kotlinx.coroutines.launch
import java.util.Date

class RegisterViewModel(private val wordRepository: WordRepository) : ViewModel() {


    fun insertWord(word: Word) {
        viewModelScope.launch {
            wordRepository.insertWord(word)
        }
    }
}

data class RegisterUiState(
    var selectedOption: Int = 0,
    var price: String = "0",
    var date: Date = Date(System.currentTimeMillis()),
    var notes: String = "",
    var category: String = "",
    var categories: List<String> = listOf()
)
