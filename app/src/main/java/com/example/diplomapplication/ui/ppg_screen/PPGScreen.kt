package com.example.diplomapplication.ui.ppg_screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PPGScreen(
    uiState: PPGScreenState,
    startRecord: () -> Unit,
    close: () -> Unit,
    closeAlert: () -> Unit,
    updateHeartRate: (Int) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val heartbeatAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.clickable(onClick = close)
                    )
                },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                uiState.textToSpeak?.let {
                    Text(
                        text = uiState.textToSpeak,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(top = 32.dp),
                    )
                }
                uiState.timerText?.let {
                    Text(
                        text = uiState.timerText,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .padding(top = 32.dp)
                    )
                }
                if (uiState.isRecording.not()) {
                    Button(
                        onClick = startRecord,
                        modifier = Modifier.padding(top = 32.dp),
                    ) {
                        Text(stringResource(R.string.ppg_start))
                    }
                }
                if (uiState.isCameraPermissionGranted) {
                    Row(
                        modifier = Modifier
                            .padding(top = 32.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier
                                .size(120.dp),
                            painter = painterResource(id = R.drawable.ppg_image),
                            contentDescription = null,
                        )
                        if (uiState.isRecording) {
                            CameraPreview(
                                updateHeartRate = updateHeartRate,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .size(120.dp)
                                    .clip(MaterialTheme.shapes.extraLarge),
                            )
                        }
                    }
                }
                uiState.heartRateText?.let {
                    Box(
                        modifier = Modifier.padding(top = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            modifier = Modifier
                                .scale(heartbeatAnimation)
                                .size(100.dp),
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primaryContainer,
                        )
                        Text(
                            text = uiState.heartRateText,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
            if (uiState.isFinishAlertVisible) {
                FinishAlert(
                    close = closeAlert,
                    finish = close,
                    heartRate = uiState.heartRateText.orEmpty(),
                )
            }
        }
    }
}

@Composable
private fun FinishAlert(
    close: () -> Unit,
    finish: () -> Unit,
    heartRate: String,
) {
    AlertDialog(
        onDismissRequest = close,
        title =  { Text(text = stringResource(R.string.ppg_alert_explanation, heartRate)) },
        confirmButton = {
            Button(
                onClick = finish,
            ) {
                Text(stringResource(R.string.ppg_alert_button))
            }
        },
    )
}
