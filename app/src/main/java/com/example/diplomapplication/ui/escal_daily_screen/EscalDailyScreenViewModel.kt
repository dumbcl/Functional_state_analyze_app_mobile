package com.example.diplomapplication.ui.escal_daily_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.data.EscalDailyResults
import com.example.diplomapplication.data.TestsRepository
import com.example.diplomapplication.ui.escal_screen.EscalScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class EscalDailyScreenViewModel(private val testsRepository: TestsRepository): ViewModel()  {

    lateinit var navController : NavController
    lateinit var showSnack: () -> Unit

    val isFinished = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(
        EscalDailyScreenState(
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
            val results = EscalDailyResults(
                performance = rawResults[0].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                fatigue = rawResults[1].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                anxiety = rawResults[2].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                conflict = rawResults[3].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                autonomy = rawResults[4].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                heteron = rawResults[5].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                eccentricity = rawResults[6].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                concetration = rawResults[7].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                vegeative = rawResults[8].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                wellbeingX = rawResults[9].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                wellbeingZ = rawResults[10].split("=")[1].takeWhile { it != '&' }.toFloat(),
                activityX = rawResults[11].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                activityZ = rawResults[12].split("=")[1].takeWhile { it != '&' }.toFloat(),
                moodX = rawResults[13].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                moodZ = rawResults[14].split("=")[1].takeWhile { it != '&' }.toFloat(),
                ipX = rawResults[15].split("=")[1].takeWhile { it != '&' }.toFloat().roundToInt(),
                ipZ = rawResults[16].split("=")[1].takeWhile { it != '&' }.toFloat()
            )
            viewModelScope.launch {
                val res = testsRepository.sendEscalDailyResults(results)
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
        val regex = Regex("Res_T=[-0-9.]+&")
        return regex.findAll(text).map { it.value }.toList()
    }
}
