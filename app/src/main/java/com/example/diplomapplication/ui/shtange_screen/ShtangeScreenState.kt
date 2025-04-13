package com.example.diplomapplication.ui.shtange_screen

data class ShtangeScreenState(
    val textToSpeak: String?,
    val secondsText: String?,
    val screenState: ScreenState,
    val heartRateText: String?,
) {
    enum class ScreenState { PRE_EXPERIMENT, EXPERIMENT, POST_EXPERIMENT }
}
