package com.example.diplomapplication.ui.ppg_screen

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PPGScreenViewModel(): ViewModel()  {

    lateinit var navController : NavController

    private val _uiState = MutableStateFlow(
        PPGScreenState(
            textToSpeak = null,
            timerText = null,
            heartRateText = null,
            isRecording = false,
            isFinishAlertVisible = false,
            isCameraPermissionGranted = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    var heartRate: Int? = null

    private var heartMeasuresCount = 0
    private var heartMeasureSum = 0

    fun startRecord() {
        startTimerDown()
    }

    private fun startTimerDown() {
        val countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.update {
                    uiState.value.copy(
                        timerText = "${millisUntilFinished / 1000}",
                        isRecording = true,
                    )
                }
            }

            override fun onFinish() {
                startTimerUp()
            }
        }.start()
    }

    private fun startTimerUp() {
        val totalTime = 30000L
        val countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedSeconds = (totalTime - millisUntilFinished) / 1000
                _uiState.update {
                    uiState.value.copy(
                        timerText = "$elapsedSeconds",
                        isRecording = true,
                    )
                }
            }

            override fun onFinish() {
                _uiState.update {
                    uiState.value.copy(
                        timerText = null,
                        isRecording = false,
                        isFinishAlertVisible = true,
                    )
                }
            }
        }.start()
    }

    fun closeFinishAlert() {
        _uiState.update {
            uiState.value.copy(
                isFinishAlertVisible = false,
            )
        }
    }

    fun close() {
        navController.popBackStack()
    }

    fun updateCameraPermission(isGranted: Boolean) {
        _uiState.update {
            uiState.value.copy(
                isCameraPermissionGranted = isGranted,
            )
        }
    }

    fun updateTextToSpeak(text: String?) {
        viewModelScope.launch {
            _uiState.update {
                uiState.value.copy(
                    textToSpeak = text,
                )
            }
        }
    }

    fun updateHeartRate(bpm: Int) {
        viewModelScope.launch {
            heartMeasuresCount++
            heartMeasureSum += bpm
            heartRate = heartMeasureSum / heartMeasuresCount
            _uiState.update {
                uiState.value.copy(
                    heartRateText = heartRate?.toString().orEmpty(),
                )
            }
        }
    }
}
