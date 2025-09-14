package com.example.pos.domain.usecase

import com.example.pos.domain.repository.TransactionRepository
import javax.inject.Inject

class sendTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
){
}