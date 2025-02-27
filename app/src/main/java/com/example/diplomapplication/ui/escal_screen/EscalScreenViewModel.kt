package com.example.diplomapplication.ui.escal_screen

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.diplomapplication.R
import com.example.diplomapplication.ui.main_screen.MainFragment

class EscalScreenViewModel(): ViewModel() {

    lateinit var navController : NavController

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
        navController.navigate(EscalFragmentDirections.actionEscalFragmentToMainFragment(uiState.value.resultCode.orEmpty()))
//        navController.popBackStack()
//        navController.popBackStack()
    }
}
