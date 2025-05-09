package com.example.diplomapplication.ui.text_audition_screen

import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.R
import com.example.diplomapplication.data.TestsRepository
import com.example.diplomapplication.data.TextAuditionTestResult
import com.example.diplomapplication.ui.text_audition_screen.TextAuditionScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TextAuditionScreenViewModel(
    private val repo: TestsRepository
) : ViewModel() {

    /* ------------ DI / utils ------------ */
    lateinit var navController: NavController
    lateinit var str: (Int) -> String
    lateinit var showSnack: () -> Unit
    var tts: TextToSpeech? = null
    val isFinished = MutableStateFlow(false)

    /** Фрагмент присвоит suspending-делегаты записи */
    var requestRecord: (suspend () -> String?)? = null   // возвращает путь к файлу

    var finishRecord: (() -> Unit)? = null

    /* ------------ UI state --------------- */
    private val _uiState = MutableStateFlow(TextAuditionScreenState())
    val uiState = _uiState.asStateFlow()

    /* ------------ данные теста ----------- */
    private lateinit var readText: String
    private lateinit var repeatText: String
    private var readTextIndex: Int = 0
    private var repeatTextIndex: Int = 0
    private var readAudioPath: String? = null
    private var repeatAudioPath: String? = null

    fun loadTexts() {
        viewModelScope.launch {
            val data = repo.getTextAuditionTest().getOrNull()   // suspend
            if (data != null) {
                readText = data.readText
                repeatText = data.repeatText
                readTextIndex = data.readTextIndex
                repeatTextIndex = data.repeatTextIndex
                val intro = str(R.string.audition_intro)
                _uiState.update { it.copy(text = intro, screenState = TextAuditionScreenState.ScreenState.READY) }
                if (tts != null) tts?.speak(uiState.value.text.orEmpty(), TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                showSnack.invoke()
            }
        }
    }

    /* ====================================================================================== */
    /*                                      UI   EVENTS                                       */
    /* ====================================================================================== */
    fun initSpeak() {
        if (uiState.value.text.isNullOrEmpty().not()) tts?.speak(uiState.value.text.orEmpty(), TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun onStartClicked() {
        _uiState.update { it.copy(text = str(R.string.reading_prepare), screenState = TextAuditionScreenState.ScreenState.READING_PREPARE) }
    }

    fun onStartRecordReading() {
        _uiState.update { it.copy(text = readText, title = str(R.string.record_is_enabled), screenState = TextAuditionScreenState.ScreenState.RECORDING) }
        startRecording { path ->
            readAudioPath = path
            //_uiState.update { it.copy(screenState = TextAuditionScreenState.ScreenState.AFTER_READING) }
        }
    }

    fun onRecordFinished() {
        if (uiState.value.text == readText) {
            _uiState.update { it.copy(text = str(R.string.after_reading), title = null, screenState = TextAuditionScreenState.ScreenState.AFTER_READING) }
            tts?.speak(str(R.string.after_reading), TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            _uiState.update { it.copy(text = str(R.string.after_repeating), title = null, screenState = TextAuditionScreenState.ScreenState.FINISHED) }
            tts?.speak(str(R.string.after_repeating), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun onContinueClicked() {
        // TTS озвучивает repeatText
        _uiState.update { it.copy(text = str(R.string.listening_prepare), screenState = TextAuditionScreenState.ScreenState.LISTENING_PREPARE) }
        tts?.speak(str(R.string.listening_prepare), TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun onStartListening() {
        // TTS озвучивает repeatText
        _uiState.update { it.copy(text = str(R.string.listen), screenState = TextAuditionScreenState.ScreenState.WAIT_REPEAT) }
        tts?.speak("$repeatText ;,\n Сейчас буду повторять;,\n $repeatText", TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun onListeningFinished() {
        _uiState.update { it.copy(text = str(R.string.audition_ready_to_repeat),
            screenState = TextAuditionScreenState.ScreenState.WAIT_REPEAT) }
        tts?.speak(str(R.string.audition_ready_to_repeat), TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun onStartRepeatRecording() {
        _uiState.update { it.copy(text = null, title = str(R.string.record_is_enabled), screenState = TextAuditionScreenState.ScreenState.RECORDING) }
        startRecording { path ->
            repeatAudioPath = path
            //_uiState.update { it.copy(screenState = TextAuditionScreenState.ScreenState.FINISHED) }
        }
    }

    fun onFinishClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(screenState = TextAuditionScreenState.ScreenState.CLOSE_LOADING) }
            val res = repo.postTextAuditionTestResults(
                TextAuditionTestResult(
                    readAudioPath = readAudioPath.orEmpty(),
                    repeatAudioPath = repeatAudioPath.orEmpty(),
                    readTextIndex = readTextIndex,
                    repeatTextIndex = repeatTextIndex,
                )
            )
            if (res.isSuccess) {
                close()
                isFinished.update { true }
            } else {
                _uiState.update { it.copy(screenState = TextAuditionScreenState.ScreenState.FINISHED) }
                showSnack.invoke()
            }
        }
    }

    fun close() {
        tts?.stop()
        navController.popBackStack()
    }

    /* ====================================================================================== */
    /*                                   INTERNAL HELPERS                                     */
    /* ====================================================================================== */

    private fun startRecording(onDone: (String?) -> Unit) {
        viewModelScope.launch {
            val path = requestRecord?.invoke()       // может вернуться null
            if (path != null) onDone(path) else showSnack()   // «Нет разрешения»
        }
    }
}
