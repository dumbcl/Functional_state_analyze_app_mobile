package com.example.diplomapplication.ui.strup_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrupScreen(
    uiState: StrupScreenState,
    start: () -> Unit,
    finish: () -> Unit,
    back: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.strup_test)) },
                navigationIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        null,
                        modifier = Modifier.clickable(onClick = back)
                    )
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                uiState.text?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 24.dp))
                }

                uiState.secondsLeft?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                when (uiState.screenState) {
                    StrupScreenState.ScreenState.READY -> {
                        Button(onClick = start) { Text(stringResource(R.string.start)) }
                    }
                    StrupScreenState.ScreenState.FINISHED -> {
                        Button(onClick = finish) { Text(stringResource(R.string.finish_test)) }
                    }
                    else -> Unit   // RUNNING – кнопок нет
                }
            }
        }
    }
}
