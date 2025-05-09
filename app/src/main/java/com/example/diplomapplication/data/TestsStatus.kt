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
    SHTANGE("shtange"),
    STRUP("strup"),
    TEXT_AUDITION("text_audition"),
    PERSONAL_REPORT("personal_report"),
}

data class TextAuditionTest(
    val readText: String,
    val repeatText: String,
    val readTextIndex: Int,
    val repeatTextIndex: Int,
)

data class TextAuditionTestResult(
    val readAudioPath: String,
    val repeatAudioPath: String,
    val readTextIndex: Int,
    val repeatTextIndex: Int,
)

data class EscalDailyResults(
    val performance: Int,
    val fatigue: Int,
    val anxiety: Int,
    val conflict: Int,
    val autonomy: Int,
    val heteron: Int,
    val eccentricity: Int,
    val concetration: Int,
    val vegeative: Int,
    val wellbeingX: Int,
    val wellbeingZ: Float,
    val activityX: Int,
    val activityZ: Float,
    val moodX: Int,
    val moodZ: Float,
    val ipX: Int,
    val ipZ: Float
)
