package com.example.pos.domain.interactor

import com.example.pos.domain.model.Transaction
import com.example.pos.domain.usecase.GetTransactionsUseCase
import com.example.pos.domain.usecase.SendTransactionUseCase
import com.example.pos.presentation.transaction.TransactionIntent
import com.example.pos.presentation.transaction.TransactionState
import javax.inject.Inject
class TransactionInteractor @Inject constructor(
    private val sendTransactionUseCase: SendTransactionUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase
) {

    suspend fun processIntent(
        intent: TransactionIntent,
        currentState: TransactionState
    ): TransactionState {
        return when (intent) {
            is TransactionIntent.AmountChanged ->
                currentState.copy(amount = intent.amount)

            is TransactionIntent.CardNumberChanged ->
                currentState.copy(cardNumber = intent.cardNumber)

            TransactionIntent.SendTransaction ->
                handleSendTransaction(currentState)

            TransactionIntent.LoadTransactions ->
                handleLoadTransactions(currentState)

            TransactionIntent.ClearError ->
                currentState.copy(error = null)
        }
    }

    private suspend fun handleSendTransaction(currentState: TransactionState): TransactionState {
        var state = currentState.copy(isLoading = true, error = null)

        try {
            val amount = state.amount.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                return state.copy(
                    isLoading = false,
                    error = "Введите корректную сумму"
                )
            }

            if (state.cardNumber.replace(" ", "").length < 16) {
                return state.copy(
                    isLoading = false,
                    error = "Номер карты должен содержать 16 цифр"
                )
            }

            val transaction = Transaction(
                cardPan = state.cardNumber.replace(" ", ""),
                amount = (amount * 100).toLong(),
                merchantId = "merchant_${System.currentTimeMillis()}"
            )

            val result = sendTransactionUseCase(transaction)

            if (result.isSuccess) {
                val transactions = getTransactionsUseCase()
                state = state.copy(
                    isLoading = false,
                    amount = "",
                    cardNumber = "",
                    transactions = transactions,
                    error = null,
                    refreshCounter = currentState.refreshCounter + 1
                )
            } else {
                state = state.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Ошибка отправки транзакции"
                )
            }

        } catch (e: Exception) {
            state = state.copy(
                isLoading = false,
                error = "Ошибка: ${e.message ?: "Неизвестная ошибка"}"
            )
        }

        return state
    }

    private suspend fun handleLoadTransactions(currentState: TransactionState): TransactionState {
        return try {
            val transactions = getTransactionsUseCase()
            currentState.copy(
                transactions = transactions,
                error = null,
                refreshCounter = currentState.refreshCounter + 1
            )
        } catch (e: Exception) {
            currentState.copy(error = "Ошибка загрузки истории транзакций: ${e.message}")
        }
    }
}