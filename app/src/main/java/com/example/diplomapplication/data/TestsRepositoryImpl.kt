package com.example.diplomapplication.data

import android.content.SharedPreferences
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.diplomapplication.data.network.ApiRepository
import com.example.diplomapplication.util.PREVIOUS_TIME
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.apply

class TestsRepositoryImpl(
    private val apiRepository: ApiRepository,
) : TestsRepository {
    override suspend fun getTestsPassingDailyStatus(): Result<TestsDailyStatusResponse> {
        return try {
            //apiRepository.getTests()
            Result.success(
                TestsDailyStatusResponse(
                    needTests = listOf(
                        Test(TestType.ESCAL, null),
                        Test(TestType.SHNTANGE, null),
                    ),
                    passedTests = listOf(
                        Test(TestType.TEXT_AUDITION, "вчера"),
                        Test(TestType.ESCAL_DAILY, "вчера"),
                        Test(TestType.REACTIONS, "вчера"),
                        Test(TestType.STRUP, "вчера"),
                        Test(TestType.RUFIE, "вчера"),
                        Test(TestType.GENCH, "вчера"),
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTimeRange(): TimeRangeFilter {

    }
}
