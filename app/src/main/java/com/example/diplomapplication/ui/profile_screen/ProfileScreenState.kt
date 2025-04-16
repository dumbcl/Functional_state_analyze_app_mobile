package com.example.diplomapplication.ui.profile_screen

data class ProfileScreenState(
    val escalType: String,
    val dayEstimates: List<DayEstimateItem>,
)

data class DayEstimateItem(
    val date: String,
    val escalDaily: String?,
    val escalDailyAverage: String?,
    val genchDaily: String?,
    val genchDailyAverage: String?,
    val reactionsDaily: String?,
    val reactionsDailyAverage: String?,
    val rufieDaily: String?,
    val rufieDailyAverage: String?,
    val shtangeDaily: String?,
    val shtangeDailyAverage: String?,
    val strupDaily: String?,
    val strupDailyAverage: String?,
    val textAuditionDaily: String?,
    val textAuditionDailyAverage: String?,
    val bogomazovDaily: String?,
    val bogomazovDailyAverage: String?,
    val personalReport: String?,
    val pulseAverageDaily: String?,
    val pulseMaxDaily: String?,
    val pulseMinDaily: String?,
    val type: EstimateType,
)

enum class EstimateType {
    GOOD, BAD, MEDIUM
}
