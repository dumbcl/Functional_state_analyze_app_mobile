package com.example.diplomapplication.ui.strup_screen

import androidx.compose.ui.graphics.Color

data class StrupScreenState(
    val description: String? = null,
    val secondsLeft: String? = null,
    val word: String? = null,
    val wordColor: Color? = null,
    val screenState: ScreenState = ScreenState.READY
) {
    enum class ScreenState { READY, RUNNING, FINISHED }
}
