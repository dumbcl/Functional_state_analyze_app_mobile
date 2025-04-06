package com.example.diplomapplication.ui.ppg_screen

data class PPGScreenState(
    val textToSpeak: String?,
    val timerText: String?,
    val heartRateText: String?,
    val isRecording: Boolean,
    val isFinishAlertVisible: Boolean,
    val isCameraPermissionGranted: Boolean,
)
