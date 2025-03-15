package com.example.diplomapplication.ui.common_elements

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.diplomapplication.R

@Composable
fun FinishAlert(
    finishTest: () -> Unit,
    closeAlert: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = closeAlert,
        title =  { Text(text = stringResource(R.string.testing_finish)) },
        confirmButton = {
            Button(
                onClick = finishTest,
            ) {
                Text(stringResource(R.string.testing_finish_yes))
            }
        },
        dismissButton = {
            Button(
                onClick = closeAlert,
            ) {
                Text(stringResource(R.string.testing_finish_no))
            }
        },
    )
}
