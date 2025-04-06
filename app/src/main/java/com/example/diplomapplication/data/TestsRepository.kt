package com.example.diplomapplication.data

import androidx.health.connect.client.time.TimeRangeFilter

interface TestsRepository {
    suspend fun getTestsPassingDailyStatus(): Result<TestsDailyStatusResponse>
}
