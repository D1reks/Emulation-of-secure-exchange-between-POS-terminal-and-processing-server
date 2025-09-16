package com.example.pos.core.network

import com.example.pos.data.remote.DTO.TransactionRequest
import com.example.pos.data.remote.DTO.TransactionResponse

interface PosClient {
    suspend fun sendTransaction(request: TransactionRequest) : TransactionResponse
    suspend fun requestKeyRotation() : Boolean
}

