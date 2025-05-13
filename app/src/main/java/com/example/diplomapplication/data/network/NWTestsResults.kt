package com.example.diplomapplication.data.network

data class NWEscalResults(
    val v1_result: Int,
    val v1_v2_result: Int,
    val v2_result: Int,
    val v2_v3_result: Int,
    val v3_result: Int,
    val v3_v4_result: Int,
    val v4_result: Int,
    val v4_v1_result: Int,
)

data class NWShtangeTestResult(
    val heart_rate_before: Int,
    val breath_hold_seconds: Int,
    val heart_rate_after: Int,
)

data class NWGenchTestResult(
    val heart_rate_before: Int,
    val breath_hold_seconds: Int,
    val heart_rate_after: Int,
)

data class NWRufieTestResult(
    val measurement_first: Int,
    val measurement_second: Int,
    val measurement_third: Int,
)

data class NWStrupTestResult(
    val result: Int,
)

data class NWPersonalReportTestResult(
    val performance_measure: Int,
    val days_comparison: String, //LOT_WORSE, WORSE, SAME, BETTER, LOT_BETTER
)

data class NWSensorimotorResponse(
    val reaction_times: List<Int>,
    val error_count: Int,
)

data class NWEscalDailyResults(
    val performance: Int,
    val fatigue: Int,
    val anxiety: Int,
    val conflict: Int,
    val autonomy: Int,
    val heteron: Int,
    val eccentricity: Int,
    val concetration: Int,
    val vegeative: Int,
    val wellbeingX: Int,
    val wellbeingZ: Float,
    val activityX: Int,
    val activityZ: Float,
    val moodX: Int,
    val moodZ: Float,
    val ipX: Int,
    val ipZ: Float
)

data class NWTextForAuditionResponse(
    val read_text: String,
    val repeat_text: String,
    val read_index: Int,
    val repeat_index: Int,
)

data class NWReactionTestResults(
    val visual: List<List<Long>>,
    val audio: List<List<Long>>,
)

data class NWTextAuditionResults(
    val read_text: String,
    val repeat_text: String,
)

data class NWDayShtangeTestResult(
    val shtange_result: String,
    val shtange_result_indicator: Float,
    val shtange_test_result_indicator_average: Float,
    val type: String
)

data class NWDayPulseMeasurementResult(
    val pulseAverage: Float,
    val pulseMax: Int,
    val pulseMin: Int,
    val type: String
)

data class NWDayRufieTestResult(
    val rufie_result: String,
    val rufie_result_indicator: Float,
    val rufie_test_result_indicator_average: Float,
    val type: String
)

data class NWDayStrupTestResult(
    val strup_result_estimation: String,
    val strup_result: Int,
    val strup_test_result_average: Float,
    val type: String
)

data class NWDayGenchTestResult(
    val gench_result_estimation: String,
    val gench_result_indicator: Float,
    val gench_test_result_indicator_average: Float,
    val type: String
)

data class NWDayReactionsTestResult(
    val reactions_visual_errors: Int,
    val reactions_audio_errors: Int,
    val reactions_visual_errors_average: Float,
    val reactions_audio_errors_average: Float,
    val reactions_visual_errors_type: String,
    val reactions_audio_errors_type: String
)

data class NWDayTextAuditionTestResult(
    val pauses_count_read: Int,
    val pauses_count_repeat: Int,
    val pauses_count_read_average: Float,
    val pauses_count_repeat_average: Float,
    val average_volume_read: Float,
    val average_volume_repeat: Float,
    val average_volume_read_average: Float,
    val average_volume_repeat_average: Float,
    val quality_read_type: String,
    val quality_repeat_type: String,
    val quality_read: Float,
    val quality_repeat: Float,
    val quality_read_average: Float,
    val quality_repeat_average: Float,
)

data class NWDayPersonalReportTestResult(
    val personal_report_about_day: String,
    val personal_report_current: Int,
    val personal_report_current_average: Float,
    val type: String
)

data class NWDayDailyTestResult(
    val date: String,
    val shtange_test_result: NWDayShtangeTestResult? = null,
    val personal_report: NWDayPersonalReportTestResult? = null,
    val pulse_measurement: NWDayPulseMeasurementResult? = null,
    val rufie_test_result: NWDayRufieTestResult? = null,
    val strup_test_result: NWDayStrupTestResult? = null,
    val gench_test_result: NWDayGenchTestResult? = null,
    val reactions_test_result: NWDayReactionsTestResult? = null,
    val text_audition_test_result: NWDayTextAuditionTestResult? = null,
    val day_description: String,
    val day_type: String? = null,
)

