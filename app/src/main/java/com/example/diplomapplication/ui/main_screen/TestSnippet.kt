package com.example.diplomapplication.ui.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R

@Composable
fun TestSnippet(
    test: TestItem,
) {
    val backgroundColor: Color
    val statusText: String

    if (test.isPassed) {
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest
        statusText = stringResource(R.string.last_attempt_day, test.date.orEmpty())
    } else {
        backgroundColor = MaterialTheme.colorScheme.primaryContainer
        statusText =
            if (test.date == null) stringResource(R.string.need_to_pass_test) else stringResource(
                R.string.last_attempt_day,
                test.date
            )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(test.titleResId),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start),
        )
        Text(
            text = stringResource(test.subtitleResId),
            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
        )

        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.End)
        )
        Button(
            onClick = { /* Realize the action here, e.g., start a test */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (test.isPassed) stringResource(R.string.pass_again) else stringResource(R.string.start_pass))
        }
    }
}
