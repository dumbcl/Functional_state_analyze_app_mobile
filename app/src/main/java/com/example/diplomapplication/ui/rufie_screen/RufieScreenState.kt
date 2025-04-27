package com.example.diplomapplication.ui.rufie_screen

data class RufieScreenState(
    val textToSpeak: String? = null,
    val secondsText: String? = null,
    val heartRateText: String? = null,
    val screenState: ScreenState = ScreenState.PRE_REST,
) {
    enum class ScreenState {
        PRE_REST,      // «Сейчас вам предстоит тест…», кнопка «Начать»
        REST,          // 5-минутный таймер «Отдыхайте»
        P1_INPUT,      // ввод P1
        PRE_EXERCISE,  // пояснение перед приседаниями
        EXERCISE,      // 45-сек «Приседайте»
        P2_INPUT,      // ввод P2
        REST_45,       // 45-сек «Отдыхайте»
        P3_INPUT       // ввод P3 и кнопка «Закончить»
    }
}
