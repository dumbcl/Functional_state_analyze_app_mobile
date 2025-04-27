package com.example.diplomapplication.data

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
