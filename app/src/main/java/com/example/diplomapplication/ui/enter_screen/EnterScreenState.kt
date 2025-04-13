package com.example.diplomapplication.ui.enter_screen

data class EnterScreenState(
    var isStartFormShown: Boolean,
    var isLoginFormShown: Boolean,
    var isRegisterFormShown: Boolean,
    var isLoginError: Boolean,
    var isRegistrationError: Boolean,
)
