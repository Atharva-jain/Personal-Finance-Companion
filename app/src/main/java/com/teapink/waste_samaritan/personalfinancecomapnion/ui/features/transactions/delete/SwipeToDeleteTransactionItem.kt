package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.delete

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.TransactionUiModel
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.items.TransactionItemUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteTransactionItem(
    transaction: TransactionUiModel, onDelete: () -> Unit, onEdit: () -> Unit, onCardClick: () -> Unit, modifier: Modifier = Modifier
) {
    var isDeleted by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                isDeleted = true
                onDelete()
                return@rememberSwipeToDismissBoxState true
            }
            false
        }
    )

    AnimatedVisibility(
        visible = !isDeleted,
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.error else Color.Transparent
                Box(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 6.dp).clip(RoundedCornerShape(20.dp)).background(color).padding(end = 24.dp),
                    contentAlignment = Alignment.CenterEnd
                ) { Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", tint = Color.White) }
            },
            enableDismissFromStartToEnd = false
        ) {
            TransactionItemUI(transaction, onEdit, onCardClick)
        }
    }
}