package com.example.diplomapplication.ui.rufie_screen

data class RufieScreenState(
    val textToSpeak: String? = null,
    val secondsText: String? = null,
    val heartRateText: String? = null,
    val screenState: ScreenState = ScreenState.PRE_REST,
) {
    enum class ScreenState {
        PRE_REST,
        REST,
        P1_INPUT,
        PRE_EXERCISE,
        EXERCISE,
        P2_INPUT,
        REST_45,
        P3_INPUT
    }
}
