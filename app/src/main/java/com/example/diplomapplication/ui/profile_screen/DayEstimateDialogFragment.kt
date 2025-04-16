package com.example.diplomapplication.ui.profile_screen

import android.R.style.Theme
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.example.diplomapplication.ui.theme.DiplomApplicationTheme
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.diplomapplication.R

class DayEstimateDialogFragment: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController = findNavController()

        return ComposeView(requireContext()).apply {
            setContent {
                DiplomApplicationTheme {
                    DayEstimateDialogScreen(
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
                        )
                    )
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}
