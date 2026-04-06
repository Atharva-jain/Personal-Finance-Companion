package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.viewmodel.TransactionViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.date.formatActiveDateRange
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.date.formatCurrency
import kotlinx.coroutines.launch
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.add.AddTransactionForm
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.delete.SwipeToDeleteTransactionItem
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.dialog.TransactionDateRangePickerDialog
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.dialog.TransactionDetailDialog
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.empty.PremiumEmptyState
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.header.DateStickyHeader
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.toggle_button.TypeToggleButton
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.date.formatRelativeDate
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// --- UI Model ---
data class TransactionUiModel(
    val id: Int,
    val amount: Double,
    val type: String,
    val category: String,
    val dateMillis: Long,
    val note: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val dateFilter by viewModel.dateFilter.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // Local UI States
    var transactionDetailsToShow by remember { mutableStateOf<TransactionUiModel?>(null) }
    var isSheetOpen by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<TransactionUiModel?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        when (actionState) {
            is Resources.Success -> viewModel.resetActionState()
            is Resources.Error -> {
                snackbarHostState.showSnackbar((actionState as Resources.Error).message ?: "An error occurred")
                viewModel.resetActionState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    transactionToEdit = null
                    isSheetOpen = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Transaction", fontWeight = FontWeight.Bold) },
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // --- Premium Search Bar ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search transactions, notes...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            // --- Scrollable Filters ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date Filter
                val dateLabel = if (dateFilter == null) "Select Date" else formatActiveDateRange(dateFilter!!.startMillis, dateFilter!!.endMillis)
                FilterChip(
                    selected = dateFilter != null,
                    onClick = { showDatePicker = true },
                    label = { Text(dateLabel) },
                    leadingIcon = { Icon(Icons.Rounded.DateRange, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (dateFilter != null) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear Date", modifier = Modifier.size(16.dp).clickable { viewModel.clearDateFilter() })
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )

                // Standard Filters
                listOf(Constants.ALL, Constants.INCOME, Constants.EXPENSE).forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.updateFilter(filter)
                        },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- UI State Handling ---
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (uiState) {
                    is Resources.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    is Resources.Error -> Text(text = (uiState as Resources.Error).message ?: "Failed to load", color = MaterialTheme.colorScheme.error)
                    is Resources.Success -> {
                        val transactions = (uiState as Resources.Success).data ?: emptyList()

                        if (transactions.isEmpty()) {
                            PremiumEmptyState()
                        } else {
                            // GROUPING LOGIC FOR STICKY HEADERS (Highly Optimized)
                            val groupedTransactions = remember(transactions) {
                                transactions.groupBy { formatRelativeDate(it.dateMillis) }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                groupedTransactions.forEach { (dateHeader, transList) ->
                                    // 1. STICKY HEADER
                                    stickyHeader {
                                        DateStickyHeader(dateText = dateHeader)
                                    }

                                    // 2. TRANSACTION LIST FOR THAT DAY
                                    items(items = transList, key = { it.id }) { transaction ->
                                        SwipeToDeleteTransactionItem(
                                            transaction = transaction,
                                            onDelete = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                viewModel.deleteTransaction(transaction.id)
                                                scope.launch {
                                                    snackbarHostState.currentSnackbarData?.dismiss()
                                                    val result = snackbarHostState.showSnackbar(message = "Transaction deleted", actionLabel = "UNDO", duration = SnackbarDuration.Short)
                                                    if (result == SnackbarResult.ActionPerformed) {
                                                        viewModel.restoreTransaction(transaction)
                                                    }
                                                }
                                            },
                                            onEdit = {
                                                transactionToEdit = transaction
                                                isSheetOpen = true
                                            },
                                            onCardClick = { transactionDetailsToShow = transaction },
                                            modifier = Modifier.animateItem()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    // --- Popups & Dialogs ---
    if (showDatePicker) {
        TransactionDateRangePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { start, end ->
                viewModel.updateDateFilter(start, end)
                showDatePicker = false
            }
        )
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false; transactionToEdit = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            AddTransactionForm(
                transactionToEdit = transactionToEdit,
                onSave = { amount, type, category, dateMillis, note ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (transactionToEdit == null) viewModel.addTransaction(amount, type, category, dateMillis, note)
                    else viewModel.updateTransaction(transactionToEdit!!.id, amount, type, category, dateMillis, note)
                    isSheetOpen = false
                    transactionToEdit = null
                }
            )
        }
    }

    AnimatedVisibility(
        visible = transactionDetailsToShow != null,
        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        transactionDetailsToShow?.let { transaction ->
            TransactionDetailDialog(
                transaction = transaction,
                onDismiss = { transactionDetailsToShow = null },
                onEdit = { transactionToEdit = transaction; isSheetOpen = true; transactionDetailsToShow = null },
                onDelete = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.deleteTransaction(transaction.id)
                    transactionDetailsToShow = null
                }
            )
        }
    }
}












