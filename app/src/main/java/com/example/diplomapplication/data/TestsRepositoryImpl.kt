package com.example.diplomapplication.data

import android.content.SharedPreferences
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.diplomapplication.data.network.ApiRepository
import com.example.diplomapplication.data.network.ApiResultState
import com.example.diplomapplication.data.network.NWEscalResults
import com.example.diplomapplication.data.network.NWHeartRateRecord
import com.example.diplomapplication.data.network.NWUserLoginRequest
import com.example.diplomapplication.util.PREVIOUS_TIME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.apply

class TestsRepositoryImpl(
    private val apiRepository: ApiRepository,
    private val sharedPreferences: SharedPreferences
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

    override suspend fun postHearRateRecords(heartRateRecords: List<HeartRateRecord.Sample>) {
        apiRepository.sendPulse(
            heartRateRecords.map { NWHeartRateRecord(it.beatsPerMinute.toInt(), it.time.toString()) }
        )
    }

    override suspend fun getHeartRateRecords() {
        apiRepository.getPulse("", "")
    }

    override suspend fun sendEscalResults(results: List<Int>) {
        if (results.size == 8) {
            apiRepository.postEscalResults(
                NWEscalResults(
                    v1_result = results[0].or(0),
                    v1_v2_result = results[1].or(0),
                    v2_result = results[2].or(0),
                    v2_v3_result = results[3].or(0),
                    v3_result = results[4].or(0),
                    v3_v4_result = results[5].or(0),
                    v4_result = results[6].or(0),
                    v4_v1_result = results[7].or(0)
                )
            )
        }
    }

    override suspend fun getEscalResult(): EscalResults {
        val results = apiRepository.getEscalResults()
        return EscalResults(
            v1Result = results.v1_result,
            v1v2Result = results.v1_v2_result,
            v2Result = results.v2_result,
            v2v3Result = results.v2_v3_result,
            v3Result = results.v3_result,
            v3v4Result = results.v3_v4_result,
            v4Result = results.v4_result,
            v4v1Result = results.v4_v1_result
        )
    }

    override suspend fun register(login: String, password: String) = flow {
        try {
            val result = apiRepository.register(NWUserLoginRequest(login, password))
            if (result.body()?.access_token == null) throw Exception("Не удалось зарегистрироваться")
            emit(ApiResultState.OnSuccess(result))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResultState.OnFailure(e.message ?: "Failed to register"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun login(login: String, password: String) = flow {
        try {
            val result = apiRepository.login(NWUserLoginRequest(login, password))
            if (result.body()?.access_token == null) throw Exception("Не удалось войти")
            sharedPreferences.edit().putString("USERNAME", login).apply()
            sharedPreferences.edit().putString("AUTH_TOKEN", result.body()?.access_token).apply()

            emit(ApiResultState.OnSuccess(result))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResultState.OnFailure(e.message ?: "Failed to login"))
        }
    }.flowOn(Dispatchers.IO)

}
