package com.example.pos.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.domain.interactor.TransactionInteractor
import com.example.pos.domain.model.Transaction
import com.example.pos.domain.usecase.SendTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.pos.domain.usecase.GetTransactionsUseCase
import com.example.pos.presentation.ui.component.ErrorDialog

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val interactor: TransactionInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionState())
    val state: StateFlow<TransactionState> = _state.asStateFlow()

    private val _intent = MutableSharedFlow<TransactionIntent>()

    init {
        viewModelScope.launch {
            _intent.collect { intent ->
                handleIntent(intent)
            }
        }

        processIntent(TransactionIntent.LoadTransactions)
    }

    private suspend fun handleIntent(intent: TransactionIntent) {
        val newState = interactor.processIntent(intent, _state.value)
        _state.update { newState }
    }

    fun processIntent(intent: TransactionIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }
}