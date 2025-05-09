package com.example.diplomapplication.ui.escal_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.data.TestsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class EscalScreenViewModel(private val testsRepository: TestsRepository): ViewModel() {

    lateinit var navController : NavController
    lateinit var showSnack: () -> Unit

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
            val rawResults = extractResTValues(code)
            val results: List<Int> = rawResults.mapNotNull { it.split("=")[1].toFloatOrNull()?.roundToInt() }
            viewModelScope.launch {
                val res = testsRepository.sendEscalResults(results)
                if (res.isSuccess) finishTest() else showSnack.invoke()
            }
        }
    }

    private fun finishTest() {
        isFinished.update { true }
        close()
    }

    private fun close() {
        navController.popBackStack()
    }

    private fun extractResTValues(text: String): List<String> {
        val regex = Regex("Res_T=\\d+")
        return regex.findAll(text).map { it.value }.toList()
    }

}
