package com.example.diplomapplication.ui.trends_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.diplomapplication.R
import com.example.diplomapplication.data.DayEscalDailyTestResult
import com.example.diplomapplication.data.DayEstimationTestResult
import com.example.diplomapplication.data.DayGenchTestResult
import com.example.diplomapplication.data.DayPersonalReport
import com.example.diplomapplication.data.DayReactionsTestResult
import com.example.diplomapplication.data.DayRufieTestResult
import com.example.diplomapplication.data.DayShtangeTestResult
import com.example.diplomapplication.data.DayTextAuditionTestResult
import com.example.diplomapplication.ui.profile_screen.ProfileScreenState
import com.example.diplomapplication.ui.rufie_screen.RufieScreenState
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.VicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendsScreen(
    uiState: TrendsScreenState,
    close: () -> Unit,
    refresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.trends_result)) },
                navigationIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable(onClick = close)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {

                when (uiState.status) {
                    TrendsScreenState.LoadingStatus.LOADING -> item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    }
                    TrendsScreenState.LoadingStatus.ERROR -> item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = refresh,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = stringResource(R.string.refresh))
                            }
                        }
                    }
                    TrendsScreenState.LoadingStatus.SUCCESS -> {
                        if (uiState.trends?.escalDaily != null && uiState.trends.personalReport != null) {
                            item {
                                val data = dayEscalDailyListToLineData(uiState.trends.escalDaily, uiState.trends.personalReport)
                                Text(
                                    text = "Оценка самочувствия",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LineChart(
                                    dataInt = data,
                                    dataFloat = null,
                                    minY = 0.0,
                                    maxY = 10.0,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        if (uiState.trends?.shtangeResult != null && uiState.trends.genchTestResult != null) {
                            item {
                                Text(
                                    text = "Физиологические показатели",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val data = dayPhysDailyListToLineData(uiState.trends.genchTestResult, uiState.trends.shtangeResult)
                                LineChart(
                                    dataInt = null,
                                    dataFloat = data,
                                    minY = 0.0,
                                    maxY = 2.0,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        if (uiState.trends?.rufieTestResult != null) {
                            item {
                                val data = rufieListToLineData(uiState.trends.rufieTestResult)
                                LineChart(
                                    dataInt = null,
                                    dataFloat = data,
                                    minY = -5.0,
                                    maxY = 18.0,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        if (uiState.trends?.reactionsResult != null) {
                            item {
                                Text(
                                    text = "Когнитивные показатели",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val data = dayReactionsListToLineData(uiState.trends.reactionsResult)
                                LineChart(
                                    dataInt = null,
                                    dataFloat = data,
                                    minY = 0.0,
                                    maxY = 1500.0,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        if (uiState.trends?.textAuditionResult != null) {
                            item {
                                val data = dayTextAuditionListToLineData(uiState.trends.textAuditionResult)
                                LineChart(
                                    dataInt = null,
                                    dataFloat = data,
                                    minY = 0.0,
                                    maxY = 1.0,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        if (uiState.trends?.estimation != null) {
                            item {
                                Text(
                                    text = "Общая оценка",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val data = dayEstimationListToLineData(uiState.trends.estimation)
                                LineChart(
                                    dataInt = data,
                                    dataFloat = null,
                                    minY = 0.0,
                                    maxY = 20.0
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

private val LegendLabelKey = ExtraStore.Key<Set<String>>()

@Composable
fun dayEscalDailyListToLineData(
    list: List<DayEscalDailyTestResult>,
    report: List<DayPersonalReport>,
): Map<String, Map<String, Int>> {
    val map = linkedMapOf<String, MutableMap<String, Int>>(
        stringResource(R.string.performance) to linkedMapOf(),
        stringResource(R.string.fatigue) to linkedMapOf(),
        stringResource(R.string.anxiety) to linkedMapOf(),
        stringResource(R.string.confliсt) to linkedMapOf(),
        stringResource(R.string.personal_report_result) to linkedMapOf(),
    )
    for (day in list) {
        map[stringResource(R.string.performance)]!![day.date] = day.performance
        map[stringResource(R.string.fatigue)]!![day.date] = day.fatigue
        map[stringResource(R.string.anxiety)]!![day.date] = day.anxiety
        map[stringResource(R.string.confliсt)]!![day.date] = day.conflict
    }
    for (day in report) {
        map[stringResource(R.string.personal_report_result)]!![day.date] = day.performanceMeasure / 10
    }
    return map
}

@Composable
fun dayPhysDailyListToLineData(
    genchList: List<DayGenchTestResult>,
    shtangeList: List<DayShtangeTestResult>,
): Map<String, Map<String, Float>> {
    val map = linkedMapOf<String, MutableMap<String, Float>>(
        stringResource(R.string.shtange_graph_result) to linkedMapOf(),
        stringResource(R.string.gench_graph_result) to linkedMapOf(),
    )
    for (day in shtangeList) {
        map[stringResource(R.string.shtange_graph_result)]!![day.date] = day.shtangeResultIndicator
    }
    for (day in genchList) {
        map[stringResource(R.string.gench_graph_result)]!![day.date] = day.genchResultIndicator
    }
    return map
}

@Composable
fun rufieListToLineData(
    rufieList: List<DayRufieTestResult>,
): Map<String, Map<String, Float>> {
    val map = linkedMapOf<String, MutableMap<String, Float>>(
        stringResource(R.string.rufie_result) to linkedMapOf(),
    )
    for (day in rufieList) {
        map[stringResource(R.string.rufie_result)]!![day.date] = day.rufieResultIndicator
    }
    return map
}

@Composable
fun dayTextAuditionListToLineData(
    textAuditionList: List<DayTextAuditionTestResult>,
): Map<String, Map<String, Float>> {
    val map = linkedMapOf<String, MutableMap<String, Float>>(
        stringResource(R.string.quality_read_text_result) to linkedMapOf(),
        stringResource(R.string.quality_repeat_text_result) to linkedMapOf(),
    )
    for (day in textAuditionList) {
        map[stringResource(R.string.quality_read_text_result)]!![day.date] = day.qualityRead
        map[stringResource(R.string.quality_repeat_text_result)]!![day.date] = day.qualityRepeat
    }
    return map
}

@Composable
fun dayEstimationListToLineData(
    estimationList: List<DayEstimationTestResult>,
): Map<String, Map<String, Int>> {
    val map = linkedMapOf<String, MutableMap<String, Int>>(
        stringResource(R.string.int_estimate_result) to linkedMapOf(),
    )
    for (day in estimationList) {
        map[stringResource(R.string.int_estimate_result)]!![day.date] = day.estimation
    }
    return map
}

@Composable
fun dayReactionsListToLineData(
    reactionsList: List<DayReactionsTestResult>,
): Map<String, Map<String, Float>> {
    val map = linkedMapOf<String, MutableMap<String, Float>>(
        stringResource(R.string.visual_reaction_avg_result) to linkedMapOf(),
        stringResource(R.string.audio_reaction_avg_result) to linkedMapOf(),
        stringResource(R.string.visual_reaction_std_result) to linkedMapOf(),
        stringResource(R.string.audio_reaction_std_result) to linkedMapOf(),
    )
    for (day in reactionsList) {
        map[stringResource(R.string.audio_reaction_avg_result)]!![day.date] = day.reactionAudioDiffAvg
        map[stringResource(R.string.audio_reaction_std_result)]!![day.date] = day.reactionAudioDiffStd
        map[stringResource(R.string.visual_reaction_avg_result)]!![day.date] = day.reactionVisualDiffAvg
        map[stringResource(R.string.visual_reaction_std_result)]!![day.date] = day.reactionVisualDiffStd
    }
    return map
}

@Composable
fun LineChart(
    dataInt: Map<String, Map<String, Int>>?,
    dataFloat: Map<String, Map<String, Float>>?,
    minY: Double,
    maxY: Double,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    // Список уникальных дат для X-оси

    val colorList = listOf(
        Color(0xFF2196F3), // blue - performance
        Color(0xFFFFA000), // orange - fatigue
        Color(0xFF4CAF50), // green - anxiety
        Color(0xFFE91E63), // pink - conflict
        Color(0xFFBBDE22), // hel - performance
    )
    val legendNames = dataInt?.keys?.toList() ?: dataFloat?.keys?.toList().orEmpty()

    LaunchedEffect(dataInt ?: dataFloat) {
        // Построим серии (важно порядок по legendNames, чтобы цвета совпали)
        modelProducer.runTransaction {
            lineSeries {
                if (dataInt != null) {
                    dataInt.forEach { (_, map) ->
                        series(map.keys.map { it.split("-")[2].toInt() }, map.values)
                    }
                } else dataFloat?.forEach { (_, map) -> series(map.keys.map { it.split("-")[2].toInt() }, map.values) }

            }
            extras { extraStore -> extraStore[LegendLabelKey] = dataInt?.keys ?: dataFloat?.keys.orEmpty() }
        }
    }

    val legendItemLabelComponent = rememberTextComponent()

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    colorList.take(legendNames.size).map { color ->
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(color)),
                            areaFill = null,
                            pointProvider = LineCartesianLayer.PointProvider.single(
                                LineCartesianLayer.point(rememberShapeComponent(fill(color), CorneredShape.Pill))
                            )
                        )
                    }
                ),
                rangeProvider =  remember { CartesianLayerRangeProvider.fixed(minY = minY, maxY = maxY, ) }
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
            legend = rememberVerticalLegend(
                items = { extraStore ->
                    legendNames.forEachIndexed { idx, label ->
                        add(
                            LegendItem(
                                shapeComponent(fill(colorList[idx])),
                                legendItemLabelComponent,
                                label
                            )
                        )
                    }
                },
                padding = insets(top = 16.dp)
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier.height(300.dp),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
    )
}
