package com.example.diplomapplication.data

import androidx.health.connect.client.records.HeartRateRecord
import com.example.diplomapplication.data.network.ApiResultState
import kotlinx.coroutines.flow.Flow

interface TestsRepository {
    suspend fun register(login: String, password: String): Flow<ApiResultState>
    suspend fun login(login: String, password: String): Flow<ApiResultState>
    suspend fun getTestsPassingDailyStatus(): Result<TestsDailyStatusResponse>
    suspend fun postHearRateRecords(heartRateRecords: List<HeartRateRecord.Sample>): Result<Unit>
    suspend fun getHeartRateRecords(): Result<Unit>
    suspend fun sendEscalResults(results: List<Int>): Result<Unit>
    suspend fun getEscalResult(): Result<EscalResults>
    suspend fun sendShtangeTestResults(results: ShtangeTestResults): Result<Unit>
    suspend fun sendGenchTestResults(results: GenchTestResults): Result<Unit>
    suspend fun sendRufieTestResults(results: RufieTestResults): Result<Unit>
    suspend fun sendReactionsTestResults(results: ReactionsTestResults): Result<Unit>
    suspend fun sendStrupTestResults(result: Int): Result<Unit>
    suspend fun getTextAuditionTest(): Result<TextAuditionTest>
    suspend fun postTextAuditionTestResults(result: TextAuditionTestResult): Result<Unit>
    suspend fun sendPersonalReport(performanceMeasure: Int, daysComparisonEnumIndex: Int): Result<Unit>
    suspend fun sendEscalDailyResults(results: EscalDailyResults): Result<Unit>
    suspend fun getTestResults(): Result<List<DayEstimate>>
    suspend fun getTrends(): Result<Trends?>
}
