package com.example.diplomapplication.ui.profile_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R

@Composable
fun ProfileScreen(
    uiState: ProfileScreenState,
    openMainScreen: () -> Unit,
    onDayClick: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = openMainScreen,
                    icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.main)) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {  },
                    icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.profile)) }
                )
            }
        }

    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Отчеты по тестированию",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                itemsIndexed(uiState.dayEstimates) { _, dayEstimate ->
                    DayEstimateRow(
                        date = dayEstimate.date,
                        type = dayEstimate.type,
                        onClick = onDayClick,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun DayEstimateRow(
    date: String,
    type: EstimateType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val stateText = when (type) {
        EstimateType.GOOD -> "Хорошее"
        EstimateType.MEDIUM -> "Удовлетворительное"
        EstimateType.BAD -> "Плохое"
    }

    val backgroundColor = when (type) {
        EstimateType.GOOD -> MaterialTheme.colorScheme.primaryContainer
        EstimateType.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
        EstimateType.BAD -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.large
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Оценка состояния: $stateText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
