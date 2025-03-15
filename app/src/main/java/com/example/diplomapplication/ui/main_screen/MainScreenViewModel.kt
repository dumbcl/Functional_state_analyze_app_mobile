package com.example.diplomapplication.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.diplomapplication.data.Test
import com.example.diplomapplication.data.TestType
import com.example.diplomapplication.data.TestsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.diplomapplication.R

class MainScreenViewModel(
    private val testsRepository: TestsRepository
): ViewModel()  {

    lateinit var navController : NavController

    private val _uiState = MutableStateFlow(
        MainScreenState(
            currentDate = getCurrentDate(),
            testsToTake = emptyList(),
            testsPassed = emptyList(),
            status = MainScreenState.LoadingStatus.LOADING,
        )
    )

    val uiState = _uiState.asStateFlow()

    fun init() = viewModelScope.launch {
        val tests = async {
            testsRepository.getTestsPassingDailyStatus()
        }.await()
        tests.fold(
            onSuccess = { tests ->
                _uiState.update {
                    uiState.value.copy(
                        testsToTake = tests.needTests.map { it.toItem(false) },
                        testsPassed = tests.passedTests.map { it.toItem(true) },
                        status = MainScreenState.LoadingStatus.SUCCESS
                    )
                }
            },
            onFailure = { error ->
                _uiState.update {
                    uiState.value.copy(
                        status = MainScreenState.LoadingStatus.ERROR
                    )
                }
            }
        )
    }

    fun refresh() = viewModelScope.launch {
        val tests = async {
            testsRepository.getTestsPassingDailyStatus()
        }.await()
        tests.fold(
            onSuccess = { tests ->
                _uiState.update {
                    uiState.value.copy(
                        testsToTake = tests.needTests.map { it.toItem(false) },
                        testsPassed = tests.passedTests.map { it.toItem(true) },
                        status = MainScreenState.LoadingStatus.SUCCESS
                    )
                }
            },
            onFailure = { error ->
                _uiState.update {
                    uiState.value.copy(
                        status = MainScreenState.LoadingStatus.ERROR
                    )
                }
            }
        )
    }

    fun navigateToProfile() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToProfileFragment())
    }

    fun navigateToPPG() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToPpgFragment())
    }

    fun navigateToEscalTesting() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToEscalFragment())
    }

    fun navigateToEscalDailyTesting() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToEscalDailyFragment())
    }

    fun navigateToGench() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToGenchFragment())
    }

    fun navigateToReactions() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToReactionsFragment())
    }

    fun navigateToRufie() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToRufieFragment())
    }

    fun navigateToShtange() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToShtangeFragment())
    }

    fun navigateToStrupTesting() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToStrupFragment())
    }

    fun navigateToTextAudition() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToTextAuditionFragment())
    }

    private fun getCurrentDate(): String {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru"))
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru"))
        val formattedDate = today.format(formatter)
        return "$formattedDate, $dayOfWeek"
    }

    private fun Test.toItem(isPassed: Boolean) = TestItem(
        type = type,
        titleResId = type.getTitle(),
        subtitleResId =  type.getSubtitle(),
        date = lastDate,
        isPassed = isPassed,
    )

    private fun TestType.getTitle() = when (this) {
        TestType.ESCAL -> R.string.test_escal_title
        TestType.ESCAL_DAILY -> R.string.test_escal_daily_title
        TestType.GENCH -> R.string.test_gench_title
        TestType.REACTIONS -> R.string.test_reactions_title
        TestType.RUFIE -> R.string.test_rufie_title
        TestType.SHNTANGE -> R.string.test_shtange_title
        TestType.STRUP -> R.string.test_strup_title
        TestType.TEXT_AUDITION -> R.string.test_text_audition_title
    }

    private fun TestType.getSubtitle() = when (this) {
        TestType.ESCAL -> R.string.test_escal_subtitle
        TestType.ESCAL_DAILY -> R.string.test_escal_daily_subtitle
        TestType.GENCH -> R.string.test_gench_subtitle
        TestType.REACTIONS -> R.string.test_reactions_subtitle
        TestType.RUFIE -> R.string.test_rufie_subtitle
        TestType.SHNTANGE -> R.string.test_shtange_subtitle
        TestType.STRUP -> R.string.test_strup_subtitle
        TestType.TEXT_AUDITION -> R.string.test_text_audition_subtitle
    }
}
