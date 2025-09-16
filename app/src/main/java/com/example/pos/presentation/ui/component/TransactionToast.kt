package com.example.pos.presentation.ui.component

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.pos.domain.model.Transaction
import com.example.pos.domain.model.TransactionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TransactionResultToast(
    transaction: Transaction?,
    onDismiss: () -> Unit
) {
    if (transaction == null) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(transaction) {
        coroutineScope.launch {
            val message = when (transaction.status) {
                TransactionStatus.APPROVED -> "Транзакция одобрена! Код: ${transaction.authCode}"
                TransactionStatus.DECLINED -> "Транзакция отклонена"
                TransactionStatus.TIMEOUT -> "Таймаут соединения"
                else -> "Транзакция обрабатывается"
            }

            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            delay(3000)
            onDismiss()
        }
    }
}