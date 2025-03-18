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

class MainScreenViewModel(
    private val testsRepository: TestsRepository
): ViewModel()  {

    lateinit var navController : NavController

    var healthConnectClient : HealthConnectClient? = null

    private val _uiState = MutableStateFlow(
        MainScreenState(
            currentDate = getCurrentDate(),
            testsToTake = emptyList(),
            testsPassed = emptyList(),
            status = MainScreenState.LoadingStatus.LOADING,
            showDownloadHealthDialog = false,
        )
    )

    val uiState = _uiState.asStateFlow()

    val isPermissionsForHealthGranted = MutableStateFlow(false)

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

    fun updateHeartRateOnServer() = viewModelScope.launch {
        try {
            val currentTime = LocalDateTime.now()
            val currentTimeInstant = currentTime.atZone(ZoneId.of("Europe/Moscow")).toInstant()
            val previousTimeInstant = currentTime.minusWeeks(2).atZone(ZoneId.of("Europe/Moscow")).toInstant()
            val timeRange = TimeRangeFilter.between(previousTimeInstant, currentTimeInstant)
            val heartRateResponse = async {
                healthConnectClient?.readRecords(
                    ReadRecordsRequest(
                        HeartRateRecord::class,
                        timeRangeFilter = timeRange,
                    )
                )
            }.await()
        } catch (e: Exception) { }
    }

    suspend fun aggregateSteps(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            // The result may be null if no data is available in the time range
            val stepCount = response[StepsRecord.COUNT_TOTAL]
        } catch (e: Exception) {
            // Run error handling here
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
