package com.example.diplomapplication.ui.shtange_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R
import com.example.diplomapplication.ui.ppg_screen.PPGScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShtangeScreen(
    uiState: ShtangeScreenState,
    closeScreen: () -> Unit,
    finishTest: () -> Unit,
    startExperiment: () -> Unit,
    stopExperiment: () -> Unit,
    openPPG: () -> Unit,
    onHeartRateChange: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("")
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable(onClick = closeScreen)
                    )
                },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
            ) {
                uiState.textToSpeak?.let {
                    Text(
                        text = uiState.textToSpeak,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                when (uiState.screenState) {
                    ShtangeScreenState.ScreenState.EXPERIMENT -> {
                        uiState.secondsText?.let {
                            Text(
                                text = uiState.secondsText,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(
                                onClick = stopExperiment,
                            ) {
                                Text(
                                    stringResource(R.string.finish)
                                )
                            }
                        }
                    }
                    ShtangeScreenState.ScreenState.PRE_EXPERIMENT -> {
                        shtangeBlocks(
                            heartRateText = uiState.heartRateText,
                            onHeartRateChange = onHeartRateChange,
                            openPPG = openPPG,
                            mainButtonText = stringResource(R.string.start),
                            mainButtonAction = startExperiment,
                        )
                    }
                    ShtangeScreenState.ScreenState.POST_EXPERIMENT -> {
                        shtangeBlocks(
                            heartRateText = uiState.heartRateText,
                            onHeartRateChange = onHeartRateChange,
                            openPPG = openPPG,
                            mainButtonText = stringResource(R.string.finish_test),
                            mainButtonAction = finishTest,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.shtangeBlocks(
    heartRateText: String?,
    onHeartRateChange: (String) -> Unit,
    openPPG: () -> Unit,
    mainButtonText: String,
    mainButtonAction: () -> Unit,
) {
    TextField(
        value = heartRateText.orEmpty(),
        onValueChange = { onHeartRateChange(it) },
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Button(
        onClick = openPPG,
        modifier = Modifier.padding(bottom = 16.dp)

    ) {
        Text(stringResource(R.string.rate_heart_by_ppg))
    }

    Button(
        onClick = mainButtonAction,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        Text(
            text = mainButtonText
        )
    }

}
