package com.example.pos.data.mapper

import com.example.pos.data.remote.DTO.TransactionRequest
import com.example.pos.domain.model.Transaction

class TransactionMapper {

    fun mapToRequest(transaction: Transaction): TransactionRequest{

        return TransactionRequest(
            cardPan = transaction.cardPan,
            amount = transaction.amount,
            merchantId = transaction.merchantId,
            transactionId = transaction.id
        )

    }

}