package com.example.diplomapplication.data

import androidx.health.connect.client.records.HeartRateRecord
import com.example.diplomapplication.data.network.ApiResultState
import kotlinx.coroutines.flow.Flow

interface TestsRepository {
    suspend fun register(login: String, password: String): Flow<ApiResultState>
    suspend fun login(login: String, password: String): Flow<ApiResultState>
    suspend fun getTestsPassingDailyStatus(): Result<TestsDailyStatusResponse>
    suspend fun postHearRateRecords(heartRateRecords: List<HeartRateRecord.Sample>)
    suspend fun getHeartRateRecords()
    suspend fun sendEscalResults(results: List<Int>)
    suspend fun getEscalResult(): EscalResults
    suspend fun sendShtangeTestResults(results: ShtangeTestResults)
    suspend fun sendGenchTestResults(results: GenchTestResults)
    suspend fun sendRufieTestResults(results: RufieTestResults)
    suspend fun sendReactionsTestResults(results: ReactionsTestResults)
}
