package com.example.diplomapplication.ui.trends_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.data.TestsRepository
import com.example.diplomapplication.ui.profile_screen.ProfileFragmentDirections
import com.example.diplomapplication.ui.profile_screen.ProfileScreenState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrendsScreenViewModel(private val testsRepository: TestsRepository): ViewModel() {

    lateinit var navController : NavController

    private val _uiState = MutableStateFlow(
        TrendsScreenState(
            status = TrendsScreenState.LoadingStatus.LOADING,
            trends = null,
        )
    )

    val uiState = _uiState.asStateFlow()

    fun init() = viewModelScope.launch {
        val results = async {
            testsRepository.getTrends()
        }.await()
        results.fold(
            onSuccess = { results ->
                _uiState.update {
                    uiState.value.copy(
                        trends = results,
                        status = TrendsScreenState.LoadingStatus.SUCCESS
                    )
                }
            },
            onFailure = { error ->
                _uiState.update {
                    uiState.value.copy(
                        status = TrendsScreenState.LoadingStatus.ERROR
                    )
                }
            }
        )
    }

    fun refresh() = viewModelScope.launch {
        val results = async {
            testsRepository.getTrends()
        }.await()
        results.fold(
            onSuccess = { results ->
                _uiState.update {
                    uiState.value.copy(
                        trends = results,
                        status = TrendsScreenState.LoadingStatus.SUCCESS
                    )
                }
            },
            onFailure = { error ->
                _uiState.update {
                    uiState.value.copy(
                        status = TrendsScreenState.LoadingStatus.ERROR
                    )
                }
            }
        )
    }

    fun close() {
        navController.popBackStack()
    }
}
