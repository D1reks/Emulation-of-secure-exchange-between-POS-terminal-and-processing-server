package com.example.pos.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pos.domain.model.Transaction
import com.example.pos.presentation.transaction.TransactionIntent
import com.example.pos.presentation.transaction.TransactionViewModel
import com.example.pos.presentation.ui.component.ErrorDialog
import com.example.pos.presentation.ui.component.TransactionForm
import com.example.pos.presentation.ui.component.TransactionHistory
import com.example.pos.presentation.ui.component.TransactionLoadingIndicator
import com.example.pos.presentation.ui.component.TransactionResultToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel
) {
    val state by viewModel.state.collectAsState()
    var lastTransaction by remember { mutableStateOf<Transaction?>(null) }

    if (state.transactions.isNotEmpty()) {
        val latestTransaction = state.transactions.last()
        if (latestTransaction != lastTransaction) {
            lastTransaction = latestTransaction
            TransactionResultToast(
                transaction = latestTransaction,
                onDismiss = { lastTransaction = null }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("POS Terminal") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TransactionForm(
                state = state,
                onAmountChange = { viewModel.processIntent(TransactionIntent.AmountChanged(it)) },
                onCardNumberChange = {
                    viewModel.processIntent(
                        TransactionIntent.CardNumberChanged(
                            it
                        )
                    )
                },
                onSendTransaction = { viewModel.processIntent(TransactionIntent.SendTransaction) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TransactionHistory(transactions = state.transactions)

            if (state.isLoading) {
                TransactionLoadingIndicator()
            }

            state.error?.let { error ->
                ErrorDialog(
                    error,
                    onDismiss = { viewModel.processIntent(TransactionIntent.ClearError) })
            }
        }
    }
}