package com.example.diplomapplication.ui.profile_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.data.TestsRepository
import com.example.diplomapplication.ui.main_screen.MainScreenState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileScreenViewModel(private val testsRepository: TestsRepository): ViewModel() {

    lateinit var navController : NavController

    private val _uiState = MutableStateFlow(
        ProfileScreenState(
            status = ProfileScreenState.LoadingStatus.LOADING,
            dayEstimates = emptyList()
        )
    )

    val uiState = _uiState.asStateFlow()

    fun init() = viewModelScope.launch {
        val results = async {
            testsRepository.getTestResults()
        }.await()
        results.fold(
            onSuccess = { results ->
                _uiState.update {
                    uiState.value.copy(
                        dayEstimates = results,
                        status = ProfileScreenState.LoadingStatus.SUCCESS
                    )
                }
            },
            onFailure = { error ->
                _uiState.update {
                    uiState.value.copy(
                        status = ProfileScreenState.LoadingStatus.ERROR
                    )
                }
            }
        )
    }

    fun refresh() = viewModelScope.launch {
        val results = async {
            testsRepository.getTestResults()
        }.await()
        results.fold(
            onSuccess = { results ->
                _uiState.update {
                    uiState.value.copy(
                        dayEstimates = results,
                        status = ProfileScreenState.LoadingStatus.SUCCESS
                    )
                }
            },
            onFailure = { error ->
                _uiState.update {
                    uiState.value.copy(
                        status = ProfileScreenState.LoadingStatus.ERROR
                    )
                }
            }
        )
    }

    fun navigateToMainScreen() {
        navController.navigate(ProfileFragmentDirections.actionProfileFragmentToMainFragment())
    }
}
