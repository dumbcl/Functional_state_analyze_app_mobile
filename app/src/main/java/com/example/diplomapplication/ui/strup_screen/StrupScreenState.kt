package com.example.diplomapplication.ui.strup_screen

data class StrupScreenState(
    val text: String? = null,
    val secondsLeft: String? = null,
    val screenState: ScreenState = ScreenState.READY
) {
    enum class ScreenState { READY, RUNNING, FINISHED }
}
