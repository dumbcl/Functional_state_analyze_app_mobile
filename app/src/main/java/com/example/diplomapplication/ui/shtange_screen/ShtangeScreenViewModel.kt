package com.example.diplomapplication.ui.shtange_screen

import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.R
import com.example.diplomapplication.data.ShtangeTestResults
import com.example.diplomapplication.data.TestsRepository
import com.example.diplomapplication.ui.main_screen.MainFragmentDirections
import com.example.diplomapplication.ui.ppg_screen.PPGScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShtangeScreenViewModel(
    private val testsRepository: TestsRepository
): ViewModel()  {

    lateinit var navController : NavController

    var tts: TextToSpeech? = null

    private val _uiState = MutableStateFlow(
        ShtangeScreenState(
            textToSpeak = null,
            secondsText = null,
            heartRateText = null,
            screenState = ShtangeScreenState.ScreenState.PRE_EXPERIMENT_CHECK
        )
    )
    val uiState = _uiState.asStateFlow()

    val isFinished = MutableStateFlow(false)

    var preExpHeartRate: Int? = null
    var postExpHeartRate: Int? = null
    var startTime: Long = 0
    var finishTime: Long = 0

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimerThread = object : Runnable {
        override fun run() {
            val timeInMillis = System.currentTimeMillis() - startTime
            val secs = (timeInMillis / 1000).toInt()
            val mins = secs / 60
            val displaySecs = secs % 60
            _uiState.update {
                uiState.value.copy(
                    secondsText = String.format("%02d:%02d", mins, displaySecs)
                )
            }

            handler.postDelayed(this, 1000)
        }
    }

    fun changeToPreExpState(textChange: String) {
        updateTextToSpeak(textChange)
        _uiState.update {
            uiState.value.copy(
                heartRateText = null,
                screenState = ShtangeScreenState.ScreenState.PRE_EXPERIMENT
            )
        }
    }

    fun startExperiment(textChange: String) {
        updateTextToSpeak(textChange)
        startTime = System.currentTimeMillis()
        preExpHeartRate = uiState.value.heartRateText?.toInt()
        _uiState.update {
            uiState.value.copy(
                heartRateText = null,
                screenState = ShtangeScreenState.ScreenState.EXPERIMENT
            )
        }
        handler.postDelayed(updateTimerThread, 0)
    }

    fun finishExperiment(textChange: String) {
        updateTextToSpeak(textChange)
        finishTime = System.currentTimeMillis()
        _uiState.update {
            uiState.value.copy(
                screenState = ShtangeScreenState.ScreenState.POST_EXPERIMENT
            )
        }
        handler.removeCallbacks(updateTimerThread)
    }

    fun updateTextToSpeak(text: String?) {
        viewModelScope.launch {
            _uiState.update {
                uiState.value.copy(
                    textToSpeak = text,
                )
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun updateHeartRateText(text: String?) {
        viewModelScope.launch {
            _uiState.update {
                uiState.value.copy(
                    heartRateText = text,
                )
            }
        }
    }

    fun finishTest() {
        postExpHeartRate = uiState.value.heartRateText?.toInt()
        viewModelScope.launch {
            testsRepository.sendShtangeTestResults(
                ShtangeTestResults(
                    heartRateBefore = preExpHeartRate,
                    secondsNumber = ((finishTime - startTime) / 1000).toInt(),
                    heartRateAfter = postExpHeartRate,
                )
            )
        }
        isFinished.update { true }
        close()
    }

    fun close() {
        navController.popBackStack()
    }

    fun openPPG() {
        navController.navigate(ShtangeFragmentDirections.actionShtangeFragmentToPpgFragment())
    }
}
