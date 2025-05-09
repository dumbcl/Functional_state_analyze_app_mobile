package com.example.diplomapplication.ui.text_audition_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R
import com.example.diplomapplication.ui.text_audition_screen.TextAuditionScreenState

interface TextAuditionActions {
    fun start()
    fun startReading()
    fun continueAfterReading()
    fun startRepeating()
    fun listeningDone()
    fun finish()
    fun back()
    fun finishRecord(): (() -> Unit)?
    fun startListening()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAuditionScreen(
    uiState: TextAuditionScreenState,
    actions: TextAuditionActions
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.txt_audition_test)) },
                navigationIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        null,
                        modifier = Modifier.clickable(onClick = {actions.back()})
                    )
                }
            )
        }
    ) { pad ->
        Box(
            Modifier
                .padding(pad)
                .fillMaxSize(),
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                uiState.text?.let {
                    Text(it, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 24.dp))
                }

                when (uiState.screenState) {
                    TextAuditionScreenState.ScreenState.READY ->
                        Button(onClick = {actions.start()}) { Text(stringResource(R.string.start)) }

                    TextAuditionScreenState.ScreenState.READING_PREPARE ->
                        Button(onClick = {actions.startReading()}) { Text(stringResource(R.string.start_record)) }

                    TextAuditionScreenState.ScreenState.AFTER_READING ->
                        Button(onClick = {actions.continueAfterReading()}) { Text(stringResource(R.string.continue_test)) }

                    TextAuditionScreenState.ScreenState.LISTENING ->
                        Button(onClick = {actions.listeningDone()}) { Text(stringResource(R.string.listening_complete)) }

                    TextAuditionScreenState.ScreenState.WAIT_REPEAT ->
                        Button(onClick = {actions.startRepeating()}) { Text(stringResource(R.string.im_ready_to_repeat)) }

                    TextAuditionScreenState.ScreenState.FINISHED ->
                        Button(onClick = {actions.finish()}) { Text(stringResource(R.string.finish_test)) }

                    TextAuditionScreenState.ScreenState.RECORDING -> {
                        Text(
                            text = stringResource(R.string.record_is_enabled),
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(onClick = { actions.finishRecord()?.invoke() }) { Text(stringResource(R.string.stop_record)) }
                    }
                    TextAuditionScreenState.ScreenState.LISTENING_PREPARE -> {
                        Button(onClick = { actions.startListening() }) { Text(stringResource(R.string.im_ready_to_listen)) }
                    }
                    TextAuditionScreenState.ScreenState.LOADING -> {}
                    TextAuditionScreenState.ScreenState.CLOSE_LOADING -> {
                        Button(onClick = {}, enabled = false) { CircularProgressIndicator() }
                    }
                }
            }
        }
    }
}
