package com.example.diplomapplication.data.network


data class NWTestsDailyStatusResponse(
    val need_tests: List<NWTest>,
    val passed_tests: List<NWTest>,
)

data class NWTest(
    val type: String? = null,
    val last_pass_date: String? = null,
)
