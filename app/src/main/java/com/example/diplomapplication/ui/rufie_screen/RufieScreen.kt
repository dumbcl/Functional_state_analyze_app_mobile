package com.example.diplomapplication.ui.rufie_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RufieScreen(
    uiState: RufieScreenState,
    closeScreen: () -> Unit,
    mainButtonClick: () -> Unit,
    onHeartRateChange: (String) -> Unit,
    openPPG: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rufie_test)) },
                navigationIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable(onClick = closeScreen)
                    )
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {

                uiState.textToSpeak?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
                }

                when (uiState.screenState) {
                    RufieScreenState.ScreenState.PRE_REST,
                    RufieScreenState.ScreenState.PRE_EXERCISE -> {
                        MainActionButton(
                            enabled = true,
                            text = if (uiState.screenState == RufieScreenState.ScreenState.PRE_REST)
                                stringResource(R.string.start)
                            else stringResource(R.string.start),
                            onClick = mainButtonClick
                        )
                    }
                    RufieScreenState.ScreenState.REST,
                    RufieScreenState.ScreenState.EXERCISE,
                    RufieScreenState.ScreenState.REST_45 -> {
                        uiState.secondsText?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                    RufieScreenState.ScreenState.P1_INPUT,
                    RufieScreenState.ScreenState.P2_INPUT,
                    RufieScreenState.ScreenState.P3_INPUT -> {
                        HeartRateInputBlock(
                            heartRateText = uiState.heartRateText,
                            onHeartRateChange = onHeartRateChange,
                            openPPG = openPPG,
                            mainButtonText = when (uiState.screenState) {
                                RufieScreenState.ScreenState.P3_INPUT -> stringResource(R.string.finish_test)
                                else -> stringResource(R.string.continue_test)
                            },
                            mainButtonClick = mainButtonClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.HeartRateInputBlock(
    heartRateText: String?,
    onHeartRateChange: (String) -> Unit,
    openPPG: () -> Unit,
    mainButtonText: String,
    mainButtonClick: () -> Unit
) {
    TextField(
        value = heartRateText.orEmpty(),
        onValueChange = { onHeartRateChange(it.filter { it.isDigit() }) },
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Button(
        onClick = openPPG,
        colors = ButtonDefaults.filledTonalButtonColors(),
        modifier = Modifier.padding(bottom = 16.dp)
    ) { Text(stringResource(R.string.rate_heart_by_ppg)) }

    MainActionButton(
        enabled = heartRateText.orEmpty().isNotEmpty(),
        text = mainButtonText,
        onClick = mainButtonClick
    )
}

@Composable
private fun ColumnScope.MainActionButton(enabled: Boolean, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) { Text(text) }
}

