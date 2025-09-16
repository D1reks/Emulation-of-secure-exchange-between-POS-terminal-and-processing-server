package com.example.pos.domain.usecase

import com.example.pos.domain.model.Transaction
import com.example.pos.domain.repository.transaction.TransactionRepository
import javax.inject.Inject

class SendTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Transaction> {
        return transactionRepository.sendTransaction(transaction)
    }
}