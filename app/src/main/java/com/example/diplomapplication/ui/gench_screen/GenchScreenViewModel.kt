package com.example.diplomapplication.ui.gench_screen

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class GenchScreenViewModel: ViewModel()  {

    lateinit var navController : NavController

    val isFinished = MutableStateFlow(false)

    fun finishTest() {
        isFinished.update { true }
        close()
    }

    fun close() {
        navController.popBackStack()
    }
}
