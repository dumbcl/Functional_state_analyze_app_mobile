package com.example.diplomapplication.ui.enter_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.data.TestsRepository
import com.example.diplomapplication.data.network.ApiResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnterViewModel(val repository: TestsRepository): ViewModel() {

    lateinit var navController : NavController
    lateinit var showSnack: () -> Unit

    private val _uiState = MutableStateFlow(
        EnterScreenState(
            isStartFormShown = true,
            isLoginFormShown = false,
            isRegisterFormShown = false,
            isLoginError = false,
            isRegistrationError = false
        )
    )

    val uiState = _uiState.asStateFlow()

    fun toLoginForm() {
        _uiState.update {
            uiState.value.copy(
                isStartFormShown = false,
                isLoginFormShown = true,
                isRegisterFormShown = false,
                isLoginError = false,
                isRegistrationError = false
            )
        }
    }

    fun toRegisterForm() {
        _uiState.update {
            uiState.value.copy(
                isStartFormShown = false,
                isLoginFormShown = false,
                isRegisterFormShown = true,
                isLoginError = false,
                isRegistrationError = false
            )
        }
    }

    fun toStartForm() {
        _uiState.update {
            uiState.value.copy(
                isStartFormShown = true,
                isLoginFormShown = false,
                isRegisterFormShown = false,
                isLoginError = false,
                isRegistrationError = false
            )
        }
    }

    fun login(name: String, password: String) {
        viewModelScope.launch {
            repository.login(name, password).onStart {
            }.catch {
                _uiState.update {
                    uiState.value.copy(
                        isStartFormShown = false,
                        isLoginFormShown = true,
                        isRegisterFormShown = false,
                        isLoginError = true,
                        isRegistrationError = false
                    )
                }
            }.collect {
                when (it) {
                    is ApiResultState.OnSuccess<*> -> {
                        _uiState.update {
                            uiState.value.copy(
                                isStartFormShown = false,
                                isLoginFormShown = true,
                                isRegisterFormShown = false,
                                isLoginError = false,
                                isRegistrationError = false
                            )
                        }
                        navigateToMainScreen()
                    }

                    is ApiResultState.OnFailure -> {
                        _uiState.update {
                            uiState.value.copy(
                                isStartFormShown = false,
                                isLoginFormShown = true,
                                isRegisterFormShown = false,
                                isLoginError = true,
                                isRegistrationError = false
                            )
                        }
                        showSnack.invoke()
                    }
                }
            }
        }
    }

    fun register(name: String, nameRep: String, password: String, passwordRep: String) {
        viewModelScope.launch {
            repository.register(name, password).onStart {
            }.catch {
                _uiState.update {
                    uiState.value.copy(
                        isStartFormShown = false,
                        isLoginFormShown = false,
                        isRegisterFormShown = true,
                        isLoginError = false,
                        isRegistrationError = true
                    )
                }
            }.collect {
                when (it) {
                    is ApiResultState.OnSuccess<*> -> {
                        _uiState.update {
                            uiState.value.copy(
                                isStartFormShown = false,
                                isLoginFormShown = true,
                                isRegisterFormShown = false,
                                isLoginError = false,
                                isRegistrationError = false
                            )
                        }
                    }

                    is ApiResultState.OnFailure -> {
                        _uiState.update {
                            uiState.value.copy(
                                isStartFormShown = false,
                                isLoginFormShown = false,
                                isRegisterFormShown = true,
                                isLoginError = false,
                                isRegistrationError = true
                            )
                        }
                        showSnack.invoke()
                    }
                }
            }
        }
    }

    fun navigateToMainScreen() {
        navController.navigate(EnterScreenFragmentDirections.actionEnterFragmentToMainFragment())
    }

}
