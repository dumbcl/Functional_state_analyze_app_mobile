package com.example.diplomapplication.ui.profile_screen

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class ProfileScreenViewModel(): ViewModel() {

    lateinit var navController : NavController

    fun navigateToMainScreen() {
        navController.navigate(ProfileFragmentDirections.actionProfileFragmentToMainFragment())
    }
}
