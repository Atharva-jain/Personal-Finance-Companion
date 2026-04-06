package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.TransactionType
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.TransactionRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.TransactionUiModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.models.DateRangeFilter



class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter

    // Tracks the state of inserts/deletes (Idle, Loading, Success, Error)
    private val _actionState = MutableStateFlow<Resources<Unit>>(Resources.Idle())
    val actionState: StateFlow<Resources<Unit>> = _actionState

    private val _dateFilter = MutableStateFlow<DateRangeFilter?>(null)
    val dateFilter: StateFlow<DateRangeFilter?> = _dateFilter

    val uiState = combine(
        repository.getAllTransactions(), // This gives List<Transaction>
        searchQuery,
        selectedFilter,
        dateFilter
    ) { resource, query, filter, dateRange ->

        // 1. Pass through Loading or Error states safely
        if (resource !is Resources.Success) {
            return@combine when(resource) {
                is Resources.Loading -> Resources.Loading()
                is Resources.Error -> Resources.Error(resource.message ?: "Unknown Error")
                else -> Resources.Idle()
            }
        }

        var filteredList = resource.data ?: emptyList()

        // 2. Apply Type Filter (FIX: Compare Enum to Enum)
        if (filter != Constants.ALL) {
            val targetType = if (filter == Constants.INCOME) TransactionType.INCOME else TransactionType.EXPENSE
            filteredList = filteredList.filter { it.type == targetType }
        }

        // 3. Apply Search Bar Filter
        if (query.isNotBlank()) {
            filteredList = filteredList.filter {
                it.category.contains(query, ignoreCase = true) ||
                        (it.note?.contains(query, ignoreCase = true) == true)
            }
        }

        // 4. Apply Date Picker Filter
        if (dateRange != null) {
            // Add 86,399,999 ms to include the very end of the selected day
            val endOfDayMillis = dateRange.endMillis + 86399999L
            filteredList = filteredList.filter {
                it.dateMillis in dateRange.startMillis..endOfDayMillis
            }
        }

        // 5. FIX: Map the Database Entity to the UI Model
        val uiModels = filteredList.map { entity ->
            TransactionUiModel(
                id = entity.id,
                amount = entity.amount,
                // Convert Enum back to String for the UI
                type = if (entity.type == TransactionType.INCOME) Constants.INCOME else Constants.EXPENSE,
                category = entity.category,
                dateMillis = entity.dateMillis,
                note = entity.note ?: "" // Safely handle the nullable note
            )
        }

        // 6. Return the perfectly mapped and filtered list!
        Resources.Success(uiModels)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resources.Loading()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun addTransaction(
        amount: Double, typeString: String, category: String, dateMillis: Long, note: String
    ) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading() // Let UI know we are saving

            val type = if (typeString.equals(
                    "Income", ignoreCase = true
                )
            ) TransactionType.INCOME else TransactionType.EXPENSE
            val newTransaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                dateMillis = dateMillis,
                note = note.ifBlank { null })

            // Save to DB and update action state with the result
            _actionState.value = repository.insertTransaction(newTransaction)
        }
    }

    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()

            // Create dummy entity with the same ID so Room knows which row to delete
            val dummyEntity = Transaction(
                id = transactionId,
                amount = 0.0,
                type = TransactionType.EXPENSE,
                category = "",
                dateMillis = 0L
            )

            _actionState.value = repository.deleteTransaction(dummyEntity)
        }
    }

    fun updateTransaction(
        id: Int,
        amount: Double,
        typeString: String,
        category: String,
        dateMillis: Long,
        note: String
    ) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()

            val type = if (typeString.equals(
                    "Income", ignoreCase = true
                )
            ) TransactionType.INCOME else TransactionType.EXPENSE

            val updatedTransaction = Transaction(
                id = id, // Pass the existing ID so Room knows to overwrite it
                amount = amount,
                type = type,
                category = category,
                dateMillis = dateMillis,
                note = note.ifBlank { null })

            _actionState.value = repository.updateTransaction(updatedTransaction)
        }
    }

    // Add this to TransactionViewModel.kt
    fun restoreTransaction(uiModel: TransactionUiModel) {
        viewModelScope.launch {
            val type = if (uiModel.type.equals("Income", ignoreCase = true)) {
                TransactionType.INCOME
            } else {
                TransactionType.EXPENSE
            }

            val transaction = Transaction(
                id = uiModel.id, // We pass the exact same ID so it restores perfectly
                amount = uiModel.amount,
                type = type,
                category = uiModel.category,
                dateMillis = uiModel.dateMillis,
                note = uiModel.note.ifBlank { null }
            )

            // Using insert here will put it right back where it was
            _actionState.value = repository.insertTransaction(transaction)
        }
    }

    // Call this after showing a Snackbar so it doesn't show again on rotation
    fun resetActionState() {
        _actionState.value = Resources.Idle()
    }


    fun updateDateFilter(start: Long, end: Long) {
        _dateFilter.value = DateRangeFilter(start, end)
    }

    fun clearDateFilter() {
        _dateFilter.value = null
    }


}