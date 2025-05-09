package com.example.diplomapplication.ui.profile_screen

import com.example.diplomapplication.data.DayEstimate

data class ProfileScreenState(
    val status: LoadingStatus,
    val dayEstimates: List<DayEstimate>,
) {
    enum class LoadingStatus { LOADING, ERROR, SUCCESS }
}
