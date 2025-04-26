package com.example.diplomapplication.ui.gench_screen

data class GenchScreenState(
    val textToSpeak: String?,
    val secondsText: String?,
    val screenState: ScreenState,
    val heartRateText: String?,
) {
    enum class ScreenState { PRE_EXPERIMENT_CHECK, PRE_EXPERIMENT, EXPERIMENT, POST_EXPERIMENT }
}
