package com.example.diplomapplication.ui.text_audition_screen

data class TextAuditionScreenState(
    val title: String? = null,
    val text: String? = null,           // описание / читаемый текст
    val screenState: ScreenState = ScreenState.LOADING
) {
    enum class ScreenState {
        LOADING,            // ждём текст из репозитория
        READY,              // описание + «Начать»
        READING_PREPARE,            // пользователь читает вслух
        RECORDING,
        AFTER_READING,      // запись сохранена, «Продолжить»
        LISTENING_PREPARE,          // TTS читает второй текст
        LISTENING,
        WAIT_REPEAT,        // «Я готов повторить»
        FINISHED,            // «Завершить тест»
        CLOSE_LOADING            // «Завершить тест»
    }
}
