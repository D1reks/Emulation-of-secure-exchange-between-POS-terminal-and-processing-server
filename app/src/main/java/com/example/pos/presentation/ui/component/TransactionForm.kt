package com.example.pos.presentation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pos.presentation.transaction.TransactionState
@Composable
fun TransactionForm(
    state: TransactionState,
    onAmountChange: (String) -> Unit,
    onCardNumberChange: (String) -> Unit,
    onSendTransaction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Новая транзакция",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = state.cardNumber,
                onValueChange = onCardNumberChange,
                label = { Text("Номер карты") },
                placeholder = { Text("4242********4242") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.amount,
                onValueChange = onAmountChange,
                label = { Text("Сумма") },
                placeholder = { Text("100.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Text("руб.") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSendTransaction,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.cardNumber.isNotEmpty() && state.amount.isNotEmpty() && !state.isLoading
            ) {
                Text("Отправить транзакцию")
            }
        }
    }
}