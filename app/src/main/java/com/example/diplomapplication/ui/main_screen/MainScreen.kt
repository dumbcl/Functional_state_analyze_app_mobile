package com.example.diplomapplication.ui.main_screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.solver.widgets.Helper
import com.example.diplomapplication.R

@Composable
fun MainScreen(
    uiState: MainScreenState,
    openProfile: () -> Unit,
    openEscal: () -> Unit,
    refresh: () -> Unit,
    closeHealthAlert: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(imageVector = Icons.Outlined.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.main)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = openProfile,
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
                        text = stringResource(R.string.greeting),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                item {
                    Text(
                        text = uiState.currentDate,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                when (uiState.status) {
                    MainScreenState.LoadingStatus.LOADING -> item {
                        CircularProgressIndicator()
                    }

                    MainScreenState.LoadingStatus.ERROR -> item {
                        Button(onClick = refresh) { Text(text = stringResource(R.string.refresh)) }
                    }
                    MainScreenState.LoadingStatus.SUCCESS -> {
                        item {
                            Text(
                                text = stringResource(R.string.test_to_pass_today),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        itemsIndexed(uiState.testsToTake) { _, item ->
                            TestSnippet(item)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Text(
                                text = stringResource(R.string.test_passed),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        itemsIndexed(uiState.testsPassed) { _, item ->
                            TestSnippet(item)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                item {
                    Button(
                        onClick = openEscal,
                    ) {
                        Text(text = stringResource(R.string.open_testing))
                    }
                }
            }

            if (uiState.showDownloadHealthDialog) {
                HealthAlert(
                    close = closeHealthAlert
                )
            }
        }
    }
}

@Composable
private fun HealthAlert(
    close: () -> Unit,
) {
    val uriString = "market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding"
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = close,
        title =  { Text(text = stringResource(R.string.health_alert_explanation)) },
        confirmButton = {
            Button(
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            setPackage("com.android.vending")
                            data = Uri.parse(uriString)
                            putExtra("overlay", true)
                            putExtra("callerId", context.packageName)
                        }
                    )
                },
            ) {
                Text(stringResource(R.string.install_health_connect))
            }
        },
    )
}
