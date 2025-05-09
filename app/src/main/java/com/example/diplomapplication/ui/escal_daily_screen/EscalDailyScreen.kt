package com.example.diplomapplication.ui.escal_daily_screen

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.example.diplomapplication.R
import com.example.diplomapplication.ui.common_elements.FinishAlert
import com.example.diplomapplication.ui.escal_screen.EscalScreenState
import com.example.diplomapplication.util.ESCAL_TEST_1ST_WALKTHROUGH_LINK
import com.example.diplomapplication.util.ESCAL_TEST_EVERY_DAY_WALKTHROUGH_LINK

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscalDailyScreen(
    uiState: EscalDailyScreenState,
    closeStartAlert: () -> Unit,
    onBackClicked: (String) -> Unit,
    closeFinishAlert: () -> Unit,
    finishTesting: () -> Unit,
) {
    var webView: WebView? by remember { mutableStateOf(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.escal_daily_testing),
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable(onClick = {
                            webView?.evaluateJavascript(
                                "(function() { return document.documentElement.outerHTML; })();"
                            ) { html ->
                                onBackClicked(html)
                            }
                        })
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = object : WebViewClient() {
                        }
                        loadUrl(ESCAL_TEST_EVERY_DAY_WALKTHROUGH_LINK)
                        webView = this
                    }
                }
            )


            if (uiState.isStartAlertVisible) {
                StartAlert(closeAlert = closeStartAlert)
            }

            if(uiState.isFinishAlertVisible) {
                FinishAlert(
                    finishTest = finishTesting,
                    closeAlert = closeFinishAlert,
                )
            }
        }
    }
}

@Composable
private fun StartAlert(
    closeAlert: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = closeAlert,
        text =  { Text(text = stringResource(R.string.escal_daily_testing_start)) },
        confirmButton = {
            Button(
                onClick = closeAlert,
            ) {
                Text(stringResource(R.string.testing_start_ready))
            }
        },
    )
}
