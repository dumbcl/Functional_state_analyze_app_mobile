package com.example.diplomapplication.ui.main_screen

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.log

class MainScreenViewModel(
    private val testsRepository: TestsRepository
): ViewModel() {

    lateinit var navController : NavController
    lateinit var showSnack: () -> Unit

    var healthConnectClient : HealthConnectClient? = null

    private val _uiState = MutableStateFlow(
        MainScreenState(
            currentDate = getCurrentDate(),
            testsToTake = emptyList(),
            testsPassed = emptyList(),
            status = MainScreenState.LoadingStatus.LOADING,
            showDownloadHealthDialog = false,
            showPersonalReport = false,
            username = ""
        )
    )

    val uiState = _uiState.asStateFlow()

    val isPermissionsForHealthGranted = MutableStateFlow(false)

    fun setUserName(login: String) {
        _uiState.update {
            uiState.value.copy(
                username = login
            )
        }
    }

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

    fun openTest(testType: TestType) = when (testType) {
        TestType.TEXT_AUDITION -> navigateToTextAudition()
        TestType.ESCAL -> navigateToEscalTesting()
        TestType.ESCAL_DAILY -> navigateToEscalDailyTesting()
        TestType.GENCH -> navigateToGench()
        TestType.REACTIONS -> navigateToReactions()
        TestType.RUFIE -> navigateToRufie()
        TestType.SHTANGE -> navigateToShtange()
        TestType.STRUP -> navigateToStrupTesting()
        TestType.PERSONAL_REPORT -> {}
    }

    fun navigateToPPG() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToPpgFragment())
    }

    private fun navigateToEscalTesting() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToEscalFragment())
    }

    fun navigateToEscalDailyTesting() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToEscalDailyFragment())
    }

    private fun navigateToGench() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToGenchFragment())
    }

    private fun navigateToReactions() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToReactionsFragment())
    }

    private fun navigateToRufie() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToRufieFragment())
    }

    private fun navigateToShtange() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToShtangeFragment())
    }

    private fun navigateToStrupTesting() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToStrupFragment())
    }

    fun navigateToTextAudition() {
        navController.navigate(MainFragmentDirections.actionMainFragmentToTextAuditionFragment())
    }

    fun showHealthDialog() {
        _uiState.update {
            uiState.value.copy(
                showDownloadHealthDialog = true,
            )
        }
    }

    fun closeHealthDialog() {
        _uiState.update {
            uiState.value.copy(
                showDownloadHealthDialog = false,
            )
        }
    }

    fun savePersonalReport(performanceMeasure: Int, daysComparisonEnumIndex: Int)  {
        viewModelScope.launch {
            try {
                val res = testsRepository.sendPersonalReport(performanceMeasure, daysComparisonEnumIndex)
                if (res.isSuccess) {
                    _uiState.update {
                        uiState.value.copy(
                            testsToTake = uiState.value.testsToTake.filter { it.type != TestType.PERSONAL_REPORT },
                        )
                    }
                } else showSnack.invoke()
            } catch (e: Exception) {
                showSnack.invoke()
            }
        }
    }

    fun updateHeartRateOnServer() = viewModelScope.launch {
        try {
            val currentTime = LocalDateTime.now()
            val currentTimeInstant = currentTime.atZone(ZoneId.of("Europe/Moscow")).toInstant()
            val previousTimeInstant = currentTime.minusWeeks(5).atZone(ZoneId.of("Europe/Moscow")).toInstant()
            val timeRange = TimeRangeFilter.between(previousTimeInstant, currentTimeInstant)
            val heartRateResponse = async {
                healthConnectClient?.readRecords(
                    ReadRecordsRequest(
                        HeartRateRecord::class,
                        timeRangeFilter = timeRange,
                    )
                )
            }.await()
            val records = heartRateResponse?.records?.flatMap { it.samples }.orEmpty()
            testsRepository.postHearRateRecords(records)
        } catch (e: Exception) {
            showSnack.invoke()
        }
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
        TestType.SHTANGE -> R.string.test_shtange_title
        TestType.STRUP -> R.string.test_strup_title
        TestType.TEXT_AUDITION -> R.string.test_text_audition_title
        TestType.PERSONAL_REPORT -> 0
    }

    private fun TestType.getSubtitle() = when (this) {
        TestType.ESCAL -> R.string.test_escal_subtitle
        TestType.ESCAL_DAILY -> R.string.test_escal_daily_subtitle
        TestType.GENCH -> R.string.test_gench_subtitle
        TestType.REACTIONS -> R.string.test_reactions_subtitle
        TestType.RUFIE -> R.string.test_rufie_subtitle
        TestType.SHTANGE -> R.string.test_shtange_subtitle
        TestType.STRUP -> R.string.test_strup_subtitle
        TestType.TEXT_AUDITION -> R.string.test_text_audition_subtitle
        TestType.PERSONAL_REPORT -> 0
    }
}
