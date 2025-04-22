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
    val days_comparison: String,
)

data class NWSensorimotorResponse(
    val reaction_times: List<Int>,
    val error_count: Int,
)
