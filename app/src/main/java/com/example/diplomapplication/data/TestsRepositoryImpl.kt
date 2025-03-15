package com.example.diplomapplication.data

import com.example.diplomapplication.data.network.ApiRepository

class TestsRepositoryImpl(
    private val apiRepository: ApiRepository
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
}
