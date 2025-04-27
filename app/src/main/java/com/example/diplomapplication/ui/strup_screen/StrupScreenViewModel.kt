package com.example.diplomapplication.ui.strup_screen

import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.R
import com.example.diplomapplication.data.TestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StrupScreenViewModel(
    private val repo: TestsRepository
) : ViewModel() {

    lateinit var navController: NavController
    lateinit var str: (Int) -> String
    var tts: TextToSpeech? = null

    private val _uiState = MutableStateFlow(StrupScreenState())
    val uiState = _uiState.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private var finishAt = 0L

    fun onStartClicked() {
        finishAt = System.currentTimeMillis() + 60_000
        _uiState.update { it.copy(screenState = StrupScreenState.ScreenState.RUNNING, text = null) }
        handler.post(ticker)
    }

    fun onFinishClicked() {
        viewModelScope.launch { repo.sendStrupTestResults(0) }
        close()
    }

    fun updateText(text: String) {
        _uiState.update { uiState.value.copy(text = text) }
    }

    fun close() {
        handler.removeCallbacksAndMessages(null)
        tts?.stop()
        navController.popBackStack()
    }

    private val ticker = object : Runnable {
        override fun run() {
            val left = (finishAt - System.currentTimeMillis()).coerceAtLeast(0)
            _uiState.update {
                it.copy(secondsLeft = "%02d:%02d".format(left / 1000 / 60, left / 1000 % 60))
            }
            if (left > 0) handler.postDelayed(this, 1000) else finishPhase()
        }
    }

    private fun finishPhase() {
        val txt = str(R.string.strup_finished)
        _uiState.update {
            it.copy(
                screenState = StrupScreenState.ScreenState.FINISHED,
                text = txt,
                secondsLeft = null
            )
        }
        tts?.speak(txt, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
