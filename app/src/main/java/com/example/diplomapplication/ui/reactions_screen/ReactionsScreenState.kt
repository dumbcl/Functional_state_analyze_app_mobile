package com.example.diplomapplication.ui.reactions_screen

import androidx.compose.ui.graphics.Color

data class ReactionsScreenState(
    val text: String? = null,
    val secondsLeft: String? = null,
    val screenState: ScreenState = ScreenState.VISUAL_READY,
    val flashColor: Color? = null           // если null – обычный фон; иначе показываем stimulus
) {
    enum class ScreenState {
        VISUAL_READY,     // описание + «Начать» (визуальные)
        VISUAL_RUNNING,   // таймер 2 мин, 30 вспышек
        AUDIO_READY,      // описание + «Начать» (аудио)
        AUDIO_RUNNING,    // таймер 2 мин, 30 сигналов
        FINISH_WAIT       // кнопка «Завершить тест»
    }
}

