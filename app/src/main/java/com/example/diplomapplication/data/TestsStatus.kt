package com.example.diplomapplication.data


data class TestsDailyStatusResponse(
    val needTests: List<Test>,
    val passedTests: List<Test>,
)

data class Test(
    val type: TestType,
    val lastDate: String?,
)

enum class TestType(val label: String) {
    ESCAL("escal"),
    ESCAL_DAILY("escal_daily"),
    GENCH("gench"),
    REACTIONS("reactions"),
    RUFIE("rufie"),
    SHNTANGE("shntange"),
    STRUP("strup"),
    TEXT_AUDITION("text_audition"),
}
