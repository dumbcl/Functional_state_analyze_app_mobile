package com.example.diplomapplication.data.network


data class NWTestsDailyStatusResponse(
    val available_tests: List<NWTest>,
    val completed_tests: List<NWTest>,
)

data class NWTest(
    val type: String? = null,
    val last_test_date: String? = null,
)
