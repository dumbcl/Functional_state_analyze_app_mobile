package com.example.diplomapplication.ui.escal_screen

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EscalScreenViewModel(): ViewModel() {

    lateinit var navController : NavController

    val isFinished = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(
        EscalScreenState(
            isStartAlertVisible = true,
            isFinishAlertVisible = false,
            resultCode = null,
        )
    )

    val uiState = _uiState.asStateFlow()

    fun closeStartAlert() {
        _uiState.update {
            uiState.value.copy(
                isStartAlertVisible = false,
            )
        }
    }

    fun setForFinish(resultCode: String) {
        _uiState.update {
            uiState.value.copy(
                isFinishAlertVisible = true,
                resultCode = resultCode,
            )
        }
    }

    fun closeFinishAlert() {
        _uiState.update {
            uiState.value.copy(
                isFinishAlertVisible = false,
            )
        }
    }

    fun finishTesting() {
        _uiState.update {
            uiState.value.copy(
                isFinishAlertVisible = false,
            )
        }
        val code = uiState.value.resultCode
        if (code != null) {
            sendCode(code)
            finishTest()
        }
    }

    private fun sendCode(code: String) {}

    private fun finishTest() {
        isFinished.update { true }
        close()
    }

    private fun close() {
        navController.popBackStack()
    }
}
