package com.example.diplomapplication.ui.profile_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.diplomapplication.data.EstimateType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(
    uiState: ProfileScreenState,
    openMainScreen: () -> Unit,
    onDayClick: (Int) -> Unit,
    refresh: () -> Unit,
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
                        text = stringResource(R.string.profile_results_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                when (uiState.status) {
                    ProfileScreenState.LoadingStatus.LOADING -> item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    }
                    ProfileScreenState.LoadingStatus.ERROR -> item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = refresh,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = stringResource(R.string.refresh))
                            }
                        }
                    }
                    ProfileScreenState.LoadingStatus.SUCCESS -> {
                        if (uiState.dayEstimates.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_tests),
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }
                        itemsIndexed(uiState.dayEstimates) { index, dayEstimate ->
                            DayEstimateRow(
                                date = dayEstimate.date,
                                type = dayEstimate.type,
                                onClick = { onDayClick.invoke(index) },
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
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
        EstimateType.GOOD -> stringResource(R.string.good_state)
        EstimateType.MEDIUM -> stringResource(R.string.medium_state)
        EstimateType.BAD -> stringResource(R.string.bad_state)
        EstimateType.UNKNOWN -> stringResource(R.string.good_state)
    }

    val backgroundColor = when (type) {
        EstimateType.GOOD -> MaterialTheme.colorScheme.primaryContainer
        EstimateType.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
        EstimateType.BAD -> MaterialTheme.colorScheme.tertiaryContainer
        EstimateType.UNKNOWN -> MaterialTheme.colorScheme.primaryContainer
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
            val dateDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val formattedDate = dateDate.format(formatter)
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = stringResource(R.string.state_evaluation, stateText),
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
