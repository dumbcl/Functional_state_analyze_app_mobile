package com.example.diplomapplication.ui.profile_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DayEstimateDialogScreen(
    item: DayEstimateItem,
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLowest

    LazyColumn(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection())
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Дата - заголовок
        item {
            Text(
                text = item.date,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
        }

        // Общая оценка состояния
        val estimateText = when (item.type) {
            EstimateType.GOOD -> "Общая оценка состояния: Хорошее"
            EstimateType.MEDIUM -> "Общая оценка состояния: Удовлетворительное"
            EstimateType.BAD -> "Общая оценка состояния: Плохое"
        }

        item {
            Text(
                text = estimateText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        item {
            Text(
                text = "Пройденные тесты:",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Тесты
        item {
            TestResultRow("Эскал", item.escalDaily, item.escalDailyAverage)
        }
        item {
            TestResultRow("Проба Генча", item.genchDaily, item.genchDailyAverage)
        }
        item {
            TestResultRow("Задания на сенсомоторную реакцию", item.reactionsDaily, item.reactionsDailyAverage)
        }
        item {
            TestResultRow("Проба Руфье", item.rufieDaily, item.rufieDailyAverage)
        }
        item {
            TestResultRow("Проба Штанге", item.shtangeDaily, item.shtangeDailyAverage)
        }
        item {
            TestResultRow("Индекс Богомазова", item.bogomazovDaily, item.bogomazovDailyAverage)
        }
        item {
            TestResultRow("Тест Струпа", item.strupDaily, item.strupDailyAverage)
        }
        item {
            TestResultRow("Задания на прочтение и повторение текста", item.textAuditionDaily, item.textAuditionDailyAverage)
        }
        item {
            TestResultRow("Пользовательская оценка", item.textAuditionDaily, item.textAuditionDailyAverage)
        }

        // Пульс
        if (
            !item.pulseAverageDaily.isNullOrEmpty() ||
            !item.pulseMaxDaily.isNullOrEmpty() ||
            !item.pulseMinDaily.isNullOrEmpty()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Пульс", style = MaterialTheme.typography.bodyLarge)
                    Column(horizontalAlignment = Alignment.End) {
                        item.pulseAverageDaily?.let {
                            Text("Среднее значение за день: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        item.pulseMaxDaily?.let {
                            Text("Максимальное значение за день: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                        item.pulseMinDaily?.let {
                            Text("Минимальное значение за день: $it", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
        item {
            Space
        }
    }
}

@Composable
fun TestResultRow(
    testName: String,
    result: String?,
    average: String?
) {
    if (result.isNullOrEmpty() && average.isNullOrEmpty()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = testName,
            style = MaterialTheme.typography.bodyLarge
        )
        Column(horizontalAlignment = Alignment.End) {
            result?.let {
                Text("Результат: $it", style = MaterialTheme.typography.bodyMedium)
            }
            average?.let {
                Text("Средний результат: $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
