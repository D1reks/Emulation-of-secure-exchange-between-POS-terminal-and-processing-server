package com.example.pos.presentation.transaction

sealed interface TransactionIntent {
    data class AmountChanged(val amount: String): TransactionIntent
    data class CardNumberChanged(val cardNumber: String): TransactionIntent
    object SendTransaction: TransactionIntent
    object LoadTransactions: TransactionIntent
    object ClearError: TransactionIntent

}