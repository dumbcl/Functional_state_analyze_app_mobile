package com.example.diplomapplication.ui.reactions_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionsScreen(
    uiState: ReactionsScreenState,
    onStart: () -> Unit,
    onTap: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reactions_test)) },
                navigationIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        null,
                        modifier = Modifier.clickable(onClick = onBack)
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(uiState.flashColor ?: MaterialTheme.colorScheme.background)
                .clickable(enabled = uiState.screenState in setOf(
                    ReactionsScreenState.ScreenState.VISUAL_RUNNING,
                    ReactionsScreenState.ScreenState.AUDIO_RUNNING
                )) { onTap() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                uiState.text?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
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
                    ReactionsScreenState.ScreenState.VISUAL_READY  -> {
                        Button(onClick = onStart) { Text(stringResource(R.string.start)) }
                    }
                    ReactionsScreenState.ScreenState.AUDIO_READY -> {
                        Button(onClick = onStart) { Text(stringResource(R.string.continue_test)) }
                    }
                    ReactionsScreenState.ScreenState.FINISH_WAIT -> {
                        Button(onClick = onFinish) { Text(stringResource(R.string.finish_test)) }
                    }
                    else -> Unit
                }
            }
        }
    }
}
