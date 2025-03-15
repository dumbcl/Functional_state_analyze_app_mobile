package com.example.diplomapplication.data

interface TestsRepository {
    suspend fun getTestsPassingDailyStatus(): Result<TestsDailyStatusResponse>
}
