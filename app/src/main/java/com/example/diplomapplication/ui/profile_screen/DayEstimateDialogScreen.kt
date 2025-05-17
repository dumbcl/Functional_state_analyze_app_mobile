package com.example.diplomapplication.ui.profile_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R
import com.example.diplomapplication.data.DayEstimate
import com.example.diplomapplication.data.EstimateType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DayEstimateDialogScreen(
    item: DayEstimate?,
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLowest

    if (item == null) return
    LazyColumn(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            val dateDate = LocalDate.parse(item.date, DateTimeFormatter.ISO_DATE)
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val formattedDate = dateDate.format(formatter)
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
        }

        item {
            val stateText = when (item.type) {
                EstimateType.GOOD -> stringResource(R.string.good_state)
                EstimateType.MEDIUM -> stringResource(R.string.medium_state)
                EstimateType.BAD -> stringResource(R.string.bad_state)
                EstimateType.UNKNOWN -> stringResource(R.string.good_state)
            }
            val estimateText = stringResource(R.string.state_evaluation, stateText)
            Text(
                text = estimateText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        if (
            item.pulseMeasurement?.pulseAverage != null ||
            item.pulseMeasurement?.pulseMax != null ||
            item.pulseMeasurement?.pulseMin != null
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.pulse), style = MaterialTheme.typography.bodyLarge)
                    Column(horizontalAlignment = Alignment.End) {
                        item.pulseMeasurement.pulseAverage.let {
                            Text(stringResource(R.string.pulse_average, String.format("%.2f", it)), style = MaterialTheme.typography.bodyMedium)
                        }
                        item.pulseMeasurement.pulseMax.let {
                            Text(stringResource(R.string.pulse_max, it), style = MaterialTheme.typography.bodyMedium)
                        }
                        item.pulseMeasurement.pulseMin.let {
                            Text(stringResource(R.string.pulse_min, it), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.passed_tests_for_a_day),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Тесты
        if (item.personalReport?.performanceMeasure != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.personal_report_result),
                    result = item.personalReport.performanceMeasure.toString(),
                    average = String.format("%.2f", item.personalReport.performanceMeasureAverage),
                    type = item.personalReport.type,
                )
            }
        }
        if (item.shtangeResult?.shtangeResultIndicator != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.shtange_result),
                    result = String.format("%.2f",item.shtangeResult.shtangeResultIndicator),
                    average = String.format("%.2f",item.shtangeResult.shtangeResultIndicatorAverage),
                    type = item.shtangeResult.type,
                )
            }
        }
        if (item.genchTestResult?.genchResultIndicator != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.gench_result),
                    result = String.format("%.2f",item.genchTestResult.genchResultIndicator),
                    average = String.format("%.2f",item.genchTestResult.genchResultIndicatorAverage),
                    type = item.genchTestResult.type,
                )
            }
        }
        if (item.rufieTestResult?.rufieResultIndicator != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.rufie_result),
                    result = String.format("%.2f",item.rufieTestResult.rufieResultIndicator),
                    average = String.format("%.2f",item.rufieTestResult.rufieResultIndicatorAverage),
                    type = item.rufieTestResult.type,
                )
            }
        }
        if (item.strupTestResult?.strupResult != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.strup_result),
                    result = item.strupTestResult.strupResult.toString(),
                    average = String.format("%.2f",item.strupTestResult.strupResultAverage),
                    type = item.strupTestResult.type,
                )
            }
        }
        if (item.reactionsResult?.reactionsAudioErrors != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.audio_reaction_result),
                    result = item.reactionsResult.reactionsAudioErrors.toString(),
                    average = String.format("%.2f",item.reactionsResult.reactionsAudioErrorsAverage),
                    type = item.reactionsResult.reactionsAudioErrorsType,
                )
            }
        }
        if (item.reactionsResult?.reactionsVisualErrors != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.video_reaction_result),
                    result = item.reactionsResult.reactionsVisualErrors.toString(),
                    average = String.format("%.2f",item.reactionsResult.reactionsVisualErrorsAverage),
                    type = item.reactionsResult.reactionsVisualErrorsType,
                )
            }
        }
        if (item.reactionsResult?.reactionAudioDiffAvg != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.audio_reaction_avg_result),
                    result = String.format("%.2f",item.reactionsResult.reactionAudioDiffAvg),
                    average = null,
                    type = EstimateType.UNKNOWN,
                )
            }
        }
        if (item.reactionsResult?.reactionAudioDiffStd != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.audio_reaction_std_result),
                    result = String.format("%.2f",item.reactionsResult.reactionAudioDiffStd),
                    average = null,
                    type = EstimateType.UNKNOWN,
                )
            }
        }
        if (item.reactionsResult?.reactionVisualDiffAvg != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.visual_reaction_avg_result),
                    result = String.format("%.2f",item.reactionsResult.reactionVisualDiffAvg),
                    average = null,
                    type = EstimateType.UNKNOWN,
                )
            }
        }
        if (item.reactionsResult?.reactionVisualDiffStd != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.visual_reaction_std_result),
                    result = String.format("%.2f",item.reactionsResult.reactionVisualDiffStd),
                    average = null,
                    type = EstimateType.UNKNOWN,
                )
            }
        }
        if (item.textAuditionResult?.qualityRead != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.quality_read_text_result),
                    result = String.format("%.2f",item.textAuditionResult.qualityRead),
                    average = String.format("%.2f",item.textAuditionResult.qualityReadAverage),
                    type = item.textAuditionResult.qualityReadType,
                )
            }
        }
        if (item.textAuditionResult?.qualityRepeat != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.quality_repeat_text_result),
                    result = String.format("%.2f",item.textAuditionResult.qualityRepeat),
                    average = String.format("%.2f",item.textAuditionResult.qualityRepeatAverage),
                    type = item.textAuditionResult.qualityRepeatType,
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.day_description),
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (item.escalDaily?.performance != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.performance),
                    result = item.escalDaily.performance.toString(),
                    average = null,
                    type = item.escalDaily.performanceType,
                )
            }
        }
        if (item.escalDaily?.fatigue != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.fatigue),
                    result = item.escalDaily.fatigue.toString(),
                    average = null,
                    type = item.escalDaily.fatigueType,
                )
            }
        }
        if (item.escalDaily?.anxiety != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.anxiety),
                    result = item.escalDaily.anxiety.toString(),
                    average = null,
                    type = item.escalDaily.anxietyType,
                )
            }
        }
        if (item.escalDaily?.conflict != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.confliсt),
                    result = item.escalDaily.conflict.toString(),
                    average = null,
                    type = item.escalDaily.conflictType,
                )
            }
        }
        if (item.escalDaily?.sanX != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.ipx),
                    result = item.escalDaily.sanX.toString(),
                    average = null,
                    type = EstimateType.UNKNOWN,
                )
            }
        }
        if (item.escalDaily?.sanZ != null) {
            item {
                TestResultRow(
                    testName = stringResource(R.string.ipz),
                    result = String.format("%.2f",item.escalDaily.sanZ),
                    average = null,
                    type = EstimateType.UNKNOWN,
                )
            }
        }


        item {
            Text(
                text = stringResource(R.string.day_description),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.dayDescription,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun TestResultRow(
    testName: String,
    result: String?,
    average: String?,
    type: EstimateType,
) {
    if (result.isNullOrEmpty() && average.isNullOrEmpty()) return
    val backgroundColor = when (type) {
        EstimateType.GOOD -> Color(0xFFC8E6C9)
        EstimateType.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
        EstimateType.BAD -> MaterialTheme.colorScheme.tertiaryContainer
        EstimateType.UNKNOWN -> MaterialTheme.colorScheme.secondaryContainer
    }
    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().background(color = backgroundColor),
    ) {
        Text(
            text = testName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Column {
            result?.let {
                Text(
                    stringResource(R.string.test_result, it),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            average?.let {
                Text(stringResource(R.string.test_result_average, it), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
