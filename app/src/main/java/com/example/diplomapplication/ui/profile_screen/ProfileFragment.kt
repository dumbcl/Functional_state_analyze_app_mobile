package com.example.diplomapplication.ui.profile_screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class ProfileFragment : Fragment()  {

    private val viewModel: ProfileScreenViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()
        viewModel.navController = navController

        val fragment = DayEstimateDialogFragment()

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    ProfileScreen(
                        uiState = ProfileScreenState(
                            "", listOf(
                                DayEstimateItem(
                                    date = "15 апреля 2025",
                                    escalDaily = null,
                                    escalDailyAverage = null,
                                    genchDaily = "46",
                                    genchDailyAverage = "42",
                                    reactionsDaily = null,
                                    reactionsDailyAverage = null,
                                    rufieDaily = "8",
                                    rufieDailyAverage = "7",
                                    shtangeDaily = "56",
                                    shtangeDailyAverage = "52",
                                    strupDaily = "77",
                                    strupDailyAverage = "68",
                                    textAuditionDaily = null,
                                    textAuditionDailyAverage = null,
                                    bogomazovDaily = null,
                                    bogomazovDailyAverage = null,
                                    personalReport = null,
                                    pulseAverageDaily = "81",
                                    pulseMaxDaily = "98",
                                    pulseMinDaily = "66",
                                    type = EstimateType.GOOD
                                ),
                                DayEstimateItem(
                                    date = "15 апреля 2025",
                                    escalDaily = null,
                                    escalDailyAverage = null,
                                    genchDaily = "46",
                                    genchDailyAverage = "42",
                                    reactionsDaily = null,
                                    reactionsDailyAverage = null,
                                    rufieDaily = "8",
                                    rufieDailyAverage = "7",
                                    shtangeDaily = "56",
                                    shtangeDailyAverage = "52",
                                    strupDaily = "77",
                                    strupDailyAverage = "68",
                                    textAuditionDaily = null,
                                    textAuditionDailyAverage = null,
                                    bogomazovDaily = null,
                                    bogomazovDailyAverage = null,
                                    personalReport = null,
                                    pulseAverageDaily = "81",
                                    pulseMaxDaily = "98",
                                    pulseMinDaily = "66",
                                    type = EstimateType.BAD
                                ),
                                DayEstimateItem(
                                    date = "15 апреля 2025",
                                    escalDaily = null,
                                    escalDailyAverage = null,
                                    genchDaily = "46",
                                    genchDailyAverage = "42",
                                    reactionsDaily = null,
                                    reactionsDailyAverage = null,
                                    rufieDaily = "8",
                                    rufieDailyAverage = "7",
                                    shtangeDaily = "56",
                                    shtangeDailyAverage = "52",
                                    strupDaily = "77",
                                    strupDailyAverage = "68",
                                    textAuditionDaily = null,
                                    textAuditionDailyAverage = null,
                                    bogomazovDaily = null,
                                    bogomazovDailyAverage = null,
                                    personalReport = null,
                                    pulseAverageDaily = "81",
                                    pulseMaxDaily = "98",
                                    pulseMinDaily = "66",
                                    type = EstimateType.MEDIUM
                                )
                            )
                        ),
                        openMainScreen = { viewModel.navigateToMainScreen() },
                        onDayClick = { fragment.show(parentFragmentManager, "DayEstimateDialog") }
                    )
                }
            }
        }
    }
}
