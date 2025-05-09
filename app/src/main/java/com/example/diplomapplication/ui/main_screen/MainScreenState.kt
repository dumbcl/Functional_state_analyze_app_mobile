package com.example.diplomapplication.ui.main_screen

import com.example.diplomapplication.data.TestType

data class MainScreenState(
    val currentDate: String,
    val username: String,
    val testsToTake: List<TestItem>,
    val testsPassed: List<TestItem>,
    val status: LoadingStatus,
    val showDownloadHealthDialog: Boolean,
    val showPersonalReport: Boolean,
) {
    enum class LoadingStatus { LOADING, ERROR, SUCCESS }
}

data class TestItem(
    val type: TestType,
    val titleResId: Int,
    val subtitleResId: Int,
    val date: String?,
    val isPassed: Boolean,
)


