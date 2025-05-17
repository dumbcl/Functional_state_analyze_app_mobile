package com.example.diplomapplication.ui.trends_screen

import com.example.diplomapplication.data.Trends

data class TrendsScreenState(
    val status: LoadingStatus,
    val trends: Trends?,
) {
    enum class LoadingStatus { LOADING, ERROR, SUCCESS }
}
