package com.firebaseapp.controlefinanceiro.recurrency

import java.time.LocalDateTime

abstract class RecurrencyModel {
    abstract val isRecurring: Boolean
    abstract val endDate: LocalDateTime?
    abstract val frequency: String
    abstract val interval: Int
    abstract val recurringGroup: String
}