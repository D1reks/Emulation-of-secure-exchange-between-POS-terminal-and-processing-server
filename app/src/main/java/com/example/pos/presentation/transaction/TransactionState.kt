package com.example.pos.presentation.transaction

import com.example.pos.domain.model.Transaction

data class TransactionState(
    val amount: String = "",
    val cardNumber: String = "",
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val error: String? = null,
    val refreshCounter: Int = 0
)
