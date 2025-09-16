package com.example.pos.presentation.ui.uitils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun maskCardNumber(cardNumber: String): String {
    return if (cardNumber.length >= 16) {
        val first4 = cardNumber.take(4)
        val last4 = cardNumber.takeLast(4)
        "$first4 **** **** $last4"
    } else {
        cardNumber
    }
}

fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        .format(Date(timestamp))
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        .format(Date(timestamp))
}