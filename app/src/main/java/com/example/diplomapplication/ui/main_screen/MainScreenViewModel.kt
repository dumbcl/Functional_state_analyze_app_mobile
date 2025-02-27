package com.example.diplomapplication.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class MainScreenViewModel(): ViewModel()  {

    lateinit var navController : NavController

    fun navigateToProfile() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToProfileFragment())
    }

    fun navigateToPPG() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToPpgFragment())
    }

    fun navigateToEscalTesting() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToEscalFragment())
    }
}
