package com.example.diplomapplication.ui.rufie_screen

import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.R
import com.example.diplomapplication.data.RufieTestResults
import com.example.diplomapplication.data.TestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class RufieScreenViewModel(
    private val testsRepository: TestsRepository,
) : ViewModel() {

    lateinit var navController: NavController
    var tts: TextToSpeech? = null
    lateinit var str: (Int) -> String
    lateinit var showSnack: () -> Unit
    val isFinished = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(RufieScreenState())
    val uiState = _uiState.asStateFlow()

    private var p1: Int? = null
    private var p2: Int? = null
    private var p3: Int? = null

    private val handler = Handler(Looper.getMainLooper())
    private var finishAt = 0L
    private val ticker = object : Runnable {
        override fun run() {
            val leftMs = (finishAt - System.currentTimeMillis()).coerceAtLeast(0)
            _uiState.update {
                it.copy(
                    secondsText = "%02d:%02d".format(
                        (leftMs / 1000 / 60), (leftMs / 1000 % 60)
                    )
                )
            }
            if (leftMs > 0) handler.postDelayed(this, 1000) else onTimerFinished()
        }
    }

    private fun startTimer(state: RufieScreenState.ScreenState, d: Duration) {
        finishAt = System.currentTimeMillis() + d.inWholeMilliseconds
        _uiState.update { it.copy(screenState = state, secondsText = null) }
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    fun onMainButtonClicked() = when (_uiState.value.screenState) {
        RufieScreenState.ScreenState.PRE_REST -> startRestPhase()
        RufieScreenState.ScreenState.P1_INPUT -> onP1Entered()
        RufieScreenState.ScreenState.PRE_EXERCISE -> startExercisePhase()
        RufieScreenState.ScreenState.P2_INPUT -> onP2Entered()
        RufieScreenState.ScreenState.P3_INPUT -> finishTest()
        else -> Unit
    }

    fun updateHeartRateText(txt: String?) =
        _uiState.update { it.copy(heartRateText = txt) }

    fun openPPG() {
        navController.navigate(RufieFragmentDirections.actionRufieFragmentToPpgFragment())
    }

    private fun startRestPhase() {
        say(R.string.rufie_explanation_rest)
        startTimer(RufieScreenState.ScreenState.REST, 5.minutes)
    }

    private fun onRestFinished() {
        say(R.string.rufie_explanation_p1_input)
        _uiState.update {
            it.copy(screenState = RufieScreenState.ScreenState.P1_INPUT, heartRateText = null)
        }
    }

    private fun onP1Entered() {
        p1 = _uiState.value.heartRateText?.toIntOrNull()
        say(R.string.rufie_explanation_pre_exercise)
        _uiState.update {
            it.copy(screenState = RufieScreenState.ScreenState.PRE_EXERCISE)
        }
    }

    private fun startExercisePhase() {
        say(R.string.rufie_explanation_exercise)
        startTimer(RufieScreenState.ScreenState.EXERCISE, 45.seconds)
    }

    private fun onExerciseFinished() {
        say(R.string.rufie_explanation_p2_input)
        _uiState.update {
            it.copy(screenState = RufieScreenState.ScreenState.P2_INPUT, heartRateText = null)
        }
    }

    private fun onP2Entered() {
        p2 = _uiState.value.heartRateText?.toIntOrNull()
        say(R.string.rufie_explanation_rest_45)
        startTimer(RufieScreenState.ScreenState.REST_45, 45.seconds)
    }

    private fun onRest45Finished() {
        say(R.string.rufie_explanation_p3_input)
        _uiState.update {
            it.copy(screenState = RufieScreenState.ScreenState.P3_INPUT, heartRateText = null)
        }
    }

    private fun finishTest() {
        p3 = _uiState.value.heartRateText?.toIntOrNull()
        viewModelScope.launch {
            val res = testsRepository.sendRufieTestResults(
                RufieTestResults(
                    heartRateRest = p1,
                    heartRateAfterExercise = p2,
                    heartRateAfterRest = p3,
                )
            )
            if (res.isSuccess) {
                close()
                isFinished.update { true }
            } else showSnack.invoke()
        }
    }

    private fun onTimerFinished() = when (_uiState.value.screenState) {
        RufieScreenState.ScreenState.REST    -> onRestFinished()
        RufieScreenState.ScreenState.EXERCISE -> onExerciseFinished()
        RufieScreenState.ScreenState.REST_45 -> onRest45Finished()
        else -> Unit
    }

    fun say(resId: Int) =
        str(resId).also { text ->
            _uiState.update { it.copy(textToSpeak = text) }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }

    fun close() {
        handler.removeCallbacks(ticker)
        navController.popBackStack()
    }
}

