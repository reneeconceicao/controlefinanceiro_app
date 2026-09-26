package com.firebaseapp.controlefinanceiro.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.repositories.WordRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val wordRepository: WordRepository): ViewModel() {

    fun insertWord(word: Word) {
        viewModelScope.launch {
            wordRepository.insertWord(word)
        }
    }
}