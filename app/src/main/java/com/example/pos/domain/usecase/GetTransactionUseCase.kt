package com.example.pos.domain.usecase

import com.example.pos.data.repository.TransactionRepositoryImpl
import com.example.pos.domain.model.Transaction
import com.example.pos.domain.repository.transaction.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepositoryImpl: TransactionRepository
) {
    suspend operator fun invoke(): List<Transaction> {
        return transactionRepositoryImpl.getTransactions()
    }
}