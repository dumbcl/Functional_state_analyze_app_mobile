package com.example.diplomapplication.ui.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R

@Composable
fun PersonalReportSnippet(
    onSave: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer

    var sliderValue by remember { mutableIntStateOf(50) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    val options = listOf(
        stringResource(R.string.personal_report_variants_one),
        stringResource(R.string.personal_report_variants_two),
        stringResource(R.string.personal_report_variants_three),
        stringResource(R.string.personal_report_variants_four),
        stringResource(R.string.personal_report_variants_five),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.personal_report_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = stringResource(R.string.personal_report_description),
            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Text(
            text = stringResource(R.string.personal_report_slider_title),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = sliderValue.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "0",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(end = 4.dp),
            )
            Slider(
                value = sliderValue.toFloat(),
                onValueChange = { sliderValue = it.toInt() },
                valueRange = 0f..100f,
                steps = 100,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "100",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        Text(
            text = stringResource(R.string.personal_report_variants_title),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedOption = index }
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == index,
                    onClick = { selectedOption = index }
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Button(
            onClick = { onSave(sliderValue, selectedOption ?: 0) },
            enabled = selectedOption != null,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}
