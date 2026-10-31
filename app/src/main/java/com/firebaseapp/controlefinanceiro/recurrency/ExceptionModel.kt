package com.firebaseapp.controlefinanceiro.recurrency

import java.time.LocalDateTime

abstract class ExceptionModel {
    abstract val wordId: Int
    abstract val occurrenceIndex: Long
    abstract val isDeleted: Boolean
    abstract val recurringGroup: String
}