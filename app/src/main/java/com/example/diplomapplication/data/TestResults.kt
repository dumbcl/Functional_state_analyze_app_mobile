package com.example.diplomapplication.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class ShtangeTestResults(
    val heartRateBefore: Int?,
    val secondsNumber: Int,
    val heartRateAfter: Int?,
)

data class GenchTestResults(
    val heartRateBefore: Int?,
    val secondsNumber: Int,
    val heartRateAfter: Int?,
)

data class RufieTestResults(
    val heartRateRest: Int?,
    val heartRateAfterExercise: Int?,
    val heartRateAfterRest: Int?,
)

data class ReactionsTestResults(
    val visual: List<Pair<Long, Long>>,
    val audio: List<Pair<Long, Long>>,
)

enum class DaysComparison {
    LOT_WORSE, WORSE, SAME, BETTER, LOT_BETTER
}

@Parcelize
data class DayEstimate(
    val date: String,
    val shtangeResult: DayShtangeTestResult?,
    val personalReport: DayPersonalReport?,
    val pulseMeasurement: DayPulseMeasurementResult?,
    val rufieTestResult: DayRufieTestResult?,
    val strupTestResult: DayStrupTestResult?,
    val genchTestResult: DayGenchTestResult?,
    val reactionsResult: DayReactionsTestResult?,
    val textAuditionResult: DayTextAuditionTestResult?,
    val escalDaily: DayEscalDailyTestResult?,
    val dayDescription: String,
    val type: EstimateType,
) : Parcelable

@Parcelize
data class DayShtangeTestResult(
    val shtangeResultIndicator: Float,
    val shtangeResultIndicatorAverage: Float,
    val type: EstimateType,
    val date: String,
) : Parcelable

@Parcelize
data class DayPulseMeasurementResult(
    val pulseAverage: Float,
    val pulseMax: Int,
    val pulseMin: Int,
    val type: EstimateType,
    val date: String,
) : Parcelable

@Parcelize
data class DayRufieTestResult(
    val rufieResultIndicator: Float,
    val rufieResultIndicatorAverage: Float,
    val type: EstimateType,
    val date: String,
) : Parcelable

@Parcelize
data class DayStrupTestResult(
    val strupResult: Int,
    val strupResultAverage: Float,
    val type: EstimateType,
    val date: String,
) : Parcelable

@Parcelize
data class DayGenchTestResult(
    val genchResultIndicator: Float,
    val genchResultIndicatorAverage: Float,
    val type: EstimateType,
    val date: String,
) : Parcelable

@Parcelize
data class DayReactionsTestResult(
    val reactionsVisualErrors: Int,
    val reactionsAudioErrors: Int,
    val reactionsVisualErrorsAverage: Float,
    val reactionsAudioErrorsAverage: Float,
    val reactionsVisualErrorsType: EstimateType,
    val reactionsAudioErrorsType: EstimateType,
    val reactionAudioDiffAvg: Float,
    val reactionVisualDiffAvg: Float,
    val reactionAudioDiffStd: Float,
    val reactionVisualDiffStd: Float,
    val date: String,
) : Parcelable

@Parcelize
data class DayTextAuditionTestResult(
    val qualityRead: Float,
    val qualityRepeat: Float,
    val qualityReadAverage: Float,
    val qualityRepeatAverage: Float,
    val qualityReadType: EstimateType,
    val qualityRepeatType: EstimateType,
    val date: String,
) : Parcelable

@Parcelize
data class DayPersonalReport(
    val performanceMeasure: Int,
    val performanceMeasureAverage: Float,
    val type: EstimateType,
    val date: String,
) : Parcelable

@Parcelize
data class DayEscalDailyTestResult(
    val performance: Int,
    val performanceType: EstimateType,
    val fatigue: Int,
    val fatigueType: EstimateType,
    val anxiety: Int,
    val anxietyType: EstimateType,
    val conflict: Int,
    val conflictType: EstimateType,
    val sanX: Int,
    val sanZ: Float,
    val date: String,
) : Parcelable

@Parcelize
data class DayEstimationTestResult(
    val estimation: Int,
    val date: String,
) : Parcelable

@Parcelize
data class Trends(
    val shtangeResult: List<DayShtangeTestResult>?,
    val personalReport: List<DayPersonalReport>?,
    val pulseMeasurement: List<DayPulseMeasurementResult>?,
    val rufieTestResult: List<DayRufieTestResult>?,
    val strupTestResult: List<DayStrupTestResult>?,
    val genchTestResult: List<DayGenchTestResult>?,
    val reactionsResult: List<DayReactionsTestResult>?,
    val textAuditionResult: List<DayTextAuditionTestResult>?,
    val escalDaily: List<DayEscalDailyTestResult>?,
    val estimation: List<DayEstimationTestResult>?,
) : Parcelable

enum class EstimateType {
    GOOD, BAD, MEDIUM, UNKNOWN
}
