package com.example.diplomapplication.ui.strup_screen

import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.R
import com.example.diplomapplication.data.TestsRepository
import com.example.diplomapplication.ui.reactions_screen.ReactionsScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TEST_DURATION_MS = 60_000L
private const val STIMULUS_COUNT = 100
private const val DISPLAY_MS = 1_000L

class StrupScreenViewModel(
    private val repo: TestsRepository
) : ViewModel() {

    /* ---------- DI-ссылки ---------- */
    lateinit var navController: NavController
    lateinit var str: (Int) -> String
    lateinit var showSnack: () -> Unit
    /** Fragment присвоит suspending-делегат распознавания */
    var requestSpeech: (suspend () -> String?)? = null
    var tts: TextToSpeech? = null

    /* ---------- UI-state ---------- */
    private val _uiState = MutableStateFlow(StrupScreenState())
    val uiState = _uiState.asStateFlow()
    val isFinished = MutableStateFlow(false)

    /* ---------- счётчики ---------- */
    private var finishAt = 0L
    private var currentIdx = 0
    private var correct = 0
    private lateinit var stimuli: List<StrupPair>

    /* ---------- таймер ---------- */
    private val handler = Handler(Looper.getMainLooper())
    private val secTicker = object : Runnable {
        override fun run() {
            val left = (finishAt - System.currentTimeMillis()).coerceAtLeast(0)
            _uiState.update {
                it.copy(secondsLeft = "%02d:%02d".format(left / 1000 / 60, left / 1000 % 60))
            }
            if (left > 0) handler.postDelayed(this, 1_000) else finishTest()
        }
    }

    /* ====================================================================================== */
    /*                                         PUBLIC                                         */
    /* ====================================================================================== */

    fun onStartClicked() {
        stimuli = generateTestSequence()
        currentIdx = 0
        correct = 0
        finishAt = System.currentTimeMillis() + TEST_DURATION_MS

        _uiState.update {
            it.copy(
                screenState = StrupScreenState.ScreenState.RUNNING,
                description = null,
                secondsLeft = null,
                word = null,
                wordColor = null
            )
        }
        handler.post(secTicker)
        showNextStimulus()
    }

    fun onFinishClicked() {
        sendAndClose()
    }

    fun close() {
        handler.removeCallbacksAndMessages(null)
        tts?.stop()
        navController.popBackStack()
    }

    /* ====================================================================================== */
    /*                                   INTERNAL  LOGIC                                      */
    /* ====================================================================================== */

    private fun generateTestSequence(): List<StrupPair> =
        List(STIMULUS_COUNT) {
            val word = STRUP_COLORS.entries.toTypedArray().random()
            val color = STRUP_COLORS.entries.toTypedArray().random()
            StrupPair(word, color)
        }

    private fun showNextStimulus() {
        if (System.currentTimeMillis() >= finishAt || currentIdx >= STIMULUS_COUNT) {
            finishTest(); return
        }

        val pair = stimuli[currentIdx]
        _uiState.update {
            it.copy(word = pair.word.label, wordColor = pair.color.compose)
        }

        handler.postDelayed({
            listenAndCheck(pair)
        }, DISPLAY_MS)
    }

    private fun listenAndCheck(pair: StrupPair) {
        viewModelScope.launch {
            val spoken = requestSpeech?.invoke()?.trim()?.lowercase()
            if (spoken == pair.color.label.lowercase()) correct++
            currentIdx++
            showNextStimulus()
        }
    }

    private fun finishTest() {
        handler.removeCallbacks(secTicker)
        val doneTxt = str(R.string.strup_finished)
        _uiState.update {
            it.copy(
                screenState = StrupScreenState.ScreenState.FINISHED,
                description = doneTxt,
                word = null,
                wordColor = null
            )
        }
        tts?.speak(doneTxt, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun sendAndClose() {
        viewModelScope.launch {
            val result = repo.sendStrupTestResults(correct)
            if (result.isSuccess) {
                close()
                isFinished.update { true }
            } else showSnack.invoke()
        }
    }

    fun speakAndSet(res: Int, updater: (StrupScreenState) -> StrupScreenState) {
        val s = str(res)
        _uiState.update { updater(it).copy(description = s) }
        tts?.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}

/* ========================================================================================== */
/*                                     DATA  CLASSES                                          */
/* ========================================================================================== */

data class StrupPair(val word: STRUP_COLORS, val color: STRUP_COLORS)

enum class STRUP_COLORS(val label: String, val compose: Color) {
    RED("красный", Color.Red),
    GREEN("зелёный", Color.Green),
    BLUE("синий", Color.Blue),
    YELLOW("жёлтый", Color.Yellow),
    PURPLE("фиолетовый", Color(0xFF800080)),
    GREY("серый", Color.Gray)
}
