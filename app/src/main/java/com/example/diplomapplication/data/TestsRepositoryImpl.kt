package com.example.diplomapplication.data

import android.content.SharedPreferences
import androidx.health.connect.client.records.HeartRateRecord
import com.example.diplomapplication.data.network.ApiRepository
import com.example.diplomapplication.data.network.ApiResultState
import com.example.diplomapplication.data.network.NWEscalDailyResults
import com.example.diplomapplication.data.network.NWEscalResults
import com.example.diplomapplication.data.network.NWGenchTestResult
import com.example.diplomapplication.data.network.NWHeartRateRecord
import com.example.diplomapplication.data.network.NWPersonalReportTestResult
import com.example.diplomapplication.data.network.NWReactionTestResults
import com.example.diplomapplication.data.network.NWRufieTestResult
import com.example.diplomapplication.data.network.NWShtangeTestResult
import com.example.diplomapplication.data.network.NWStrupTestResult
import com.example.diplomapplication.data.network.NWUserLoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class TestsRepositoryImpl(
    private val apiRepository: ApiRepository,
    private val sharedPreferences: SharedPreferences
) : TestsRepository {
    override suspend fun getTestsPassingDailyStatus(): Result<TestsDailyStatusResponse> {
        return try {
            val res = apiRepository.getTests()
            Result.success(
                TestsDailyStatusResponse(
                    needTests = res.available_tests.mapNotNull { test ->
                        if (test.type != null) {
                            Test(
                                type = TestType.valueOf(test.type.toUpperCase()),
                                lastDate = test.last_test_date,
                            )
                        } else {
                            null
                        }
                    },
                    passedTests = res.completed_tests.mapNotNull { test ->
                        if (test.type != null) {
                            Test(
                                type = TestType.valueOf(test.type.toUpperCase()),
                                lastDate = test.last_test_date,
                            )
                        } else {
                            null
                        }
                    }
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun postHearRateRecords(heartRateRecords: List<HeartRateRecord.Sample>): Result<Unit> {
        return try {
            apiRepository.sendPulse(
                heartRateRecords.map { NWHeartRateRecord(it.beatsPerMinute.toInt(), it.time.toString()) }
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHeartRateRecords(): Result<Unit> {
        return try {
            apiRepository.getPulse("", "")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendEscalResults(results: List<Int>): Result<Unit> {
        return try {
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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEscalResult(): Result<EscalResults> {
        return try {
            val results = apiRepository.getEscalResults()
            Result.success(EscalResults(
                v1Result = results.v1_result,
                v1v2Result = results.v1_v2_result,
                v2Result = results.v2_result,
                v2v3Result = results.v2_v3_result,
                v3Result = results.v3_result,
                v3v4Result = results.v3_v4_result,
                v4Result = results.v4_result,
                v4v1Result = results.v4_v1_result
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendShtangeTestResults(results: ShtangeTestResults): Result<Unit> {
        return try {
            apiRepository.postShtangeTestResult(NWShtangeTestResult(
                heart_rate_before = results.heartRateBefore ?: 0,
                heart_rate_after = results.heartRateAfter ?: 0,
                breath_hold_seconds = results.secondsNumber,
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendGenchTestResults(results: GenchTestResults): Result<Unit> {
        return try {
            apiRepository.postGenchTestResult(NWGenchTestResult(
                heart_rate_before = results.heartRateBefore ?: 0,
                heart_rate_after = results.heartRateAfter ?: 0,
                breath_hold_seconds = results.secondsNumber,
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendRufieTestResults(results: RufieTestResults): Result<Unit> {
        return try {
            apiRepository.postRufieTestResult(NWRufieTestResult(
                measurement_first = results.heartRateRest ?: 0 ,
                measurement_second = results.heartRateAfterExercise ?: 0 ,
                measurement_third = results.heartRateAfterRest ?: 0
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendReactionsTestResults(results: ReactionsTestResults): Result<Unit> {
        return try {
            apiRepository.postReactionTestResults(NWReactionTestResults(
                visual = results.visual.map { listOf(it.first, it.second) },
                audio = results.audio.map { listOf(it.first, it.second) },
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendStrupTestResults(result: Int): Result<Unit> {
        return try {
            apiRepository.postStrupTestResult(NWStrupTestResult(
                result = result,
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTextAuditionTest(): Result<TextAuditionTest> {
        return try {
            val result = apiRepository.getTextsForAuditions()
            Result.success(TextAuditionTest(
                readText = result.read_text,
                repeatText = result.repeat_text,
                readTextIndex = result.read_index,
                repeatTextIndex = result.repeat_index,
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun postTextAuditionTestResults(result: TextAuditionTestResult): Result<Unit> {
        return try {
            val mediaType = "audio/m4a".toMediaType()
            val readAudioFile = File(result.readAudioPath)
            val repeatAudioFile = File(result.repeatAudioPath)
            val requestRepeatAudioFile = readAudioFile.asRequestBody(mediaType)
            val requestReadAudioFile = repeatAudioFile.asRequestBody(mediaType)

            val readPartFile = MultipartBody.Part.createFormData("read_text_file", readAudioFile.name, requestReadAudioFile)
            val repeatPartFile = MultipartBody.Part.createFormData("repeat_text_file", repeatAudioFile.name, requestRepeatAudioFile)

            val readTextIndex = RequestBody.create(MultipartBody.FORM, "${result.readTextIndex}")  // Значение для read_text_index
            val repeatTextIndex = RequestBody.create(MultipartBody.FORM, "${result.repeatTextIndex}")  // Значение для repeat_text_index

            apiRepository.postTextAuditionResults(
                read_text_index = readTextIndex,
                repeat_text_index = repeatTextIndex,
                read_text_file = readPartFile,
                repeat_text_file = repeatPartFile
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPersonalReport(
        performanceMeasure: Int,
        daysComparisonEnumIndex: Int
    ): Result<Unit> {
        return try {
            apiRepository.postPersonalReportTestResult(
                NWPersonalReportTestResult(
                    performance_measure = performanceMeasure,
                    days_comparison = daysComparisonEnumIndex.toDaysComparison(),
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendEscalDailyResults(results: EscalDailyResults): Result<Unit> {
        return try {
            apiRepository.postEscalDailyResults(
                NWEscalDailyResults(
                    performance = results.performance,
                    fatigue = results.fatigue,
                    anxiety = results.anxiety,
                    conflict = results.conflict,
                    autonomy = results.autonomy,
                    heteron = results.heteron,
                    eccentricity = results.eccentricity,
                    concetration = results.concetration,
                    vegeative = results.vegeative,
                    wellbeingX = results.wellbeingX,
                    wellbeingZ = results.wellbeingZ,
                    activityX = results.activityX,
                    activityZ = results.activityZ,
                    moodX = results.moodX,
                    moodZ = results.moodZ,
                    ipX = results.ipX,
                    ipZ = results.ipZ
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    override suspend fun getTestResults(): Result<List<DayEstimate>> {
        return try {
            val result = apiRepository.getResults().orEmpty()
            Result.success(
                result.mapNotNull { result ->
                    if (result != null) {
                        DayEstimate(
                            date = result.date,
                            shtangeResult = if (result.shtange_test_result != null) {
                                DayShtangeTestResult(
                                    shtangeResultIndicator = result.shtange_test_result.shtange_result_indicator,
                                    shtangeResultIndicatorAverage = result.shtange_test_result.shtange_test_result_indicator_average,
                                    type = EstimateType.entries.toTypedArray().find {
                                        it.name == result.shtange_test_result.type
                                    } ?: EstimateType.UNKNOWN,
                                    date = result.shtange_test_result.date.orEmpty(),
                                )
                            } else null,
                            personalReport = if (result.personal_report != null) {
                                DayPersonalReport(
                                    performanceMeasure = result.personal_report.personal_report_current,
                                    performanceMeasureAverage = result.personal_report.personal_report_current_average,
                                    type = EstimateType.entries.toTypedArray().find {
                                        it.name == result.personal_report.type
                                    } ?: EstimateType.UNKNOWN,
                                    date = result.personal_report.date.orEmpty(),
                                )
                            } else null,
                            pulseMeasurement = if (result.pulse_measurement != null) {
                                DayPulseMeasurementResult(
                                    pulseAverage = result.pulse_measurement.pulseAverage,
                                    pulseMax = result.pulse_measurement.pulseMax,
                                    pulseMin = result.pulse_measurement.pulseMin,
                                    type = EstimateType.entries.toTypedArray().find {
                                        it.name == result.pulse_measurement.type
                                    } ?: EstimateType.UNKNOWN,
                                    date = result.pulse_measurement.date.orEmpty(),
                                )
                            } else null,
                            rufieTestResult = if (result.rufie_test_result != null) {
                                DayRufieTestResult(
                                    rufieResultIndicator = result.rufie_test_result.rufie_result_indicator,
                                    rufieResultIndicatorAverage = result.rufie_test_result.rufie_test_result_indicator_average,
                                    type = EstimateType.entries.toTypedArray().find {
                                        it.name == result.rufie_test_result.type
                                    } ?: EstimateType.UNKNOWN,
                                    date = result.rufie_test_result.date.orEmpty(),
                                )
                            } else null,
                            strupTestResult = if (result.strup_test_result != null) {
                                DayStrupTestResult(
                                    strupResult = result.strup_test_result.strup_result,
                                    strupResultAverage = result.strup_test_result.strup_test_result_average,
                                    type = EstimateType.entries.toTypedArray().find {
                                        it.name == result.strup_test_result.type
                                    } ?: EstimateType.UNKNOWN,
                                    date = result.strup_test_result.date.orEmpty(),
                                )
                            } else null,
                            genchTestResult = if (result.gench_test_result != null) {
                                DayGenchTestResult(
                                    genchResultIndicator = result.gench_test_result.gench_result_indicator,
                                    genchResultIndicatorAverage = result.gench_test_result.gench_test_result_indicator_average,
                                    type = EstimateType.entries.toTypedArray().find {
                                        it.name == result.gench_test_result.type
                                    } ?: EstimateType.UNKNOWN,
                                    date = result.gench_test_result.date.orEmpty(),
                                )
                            } else null,
                            reactionsResult = if (result.reactions_test_result != null) {
                                DayReactionsTestResult(
                                    reactionsVisualErrors = result.reactions_test_result.reactions_visual_errors,
                                    reactionsAudioErrors = result.reactions_test_result.reactions_audio_errors,
                                    reactionsVisualErrorsAverage = result.reactions_test_result.reactions_visual_errors_average,
                                    reactionsAudioErrorsAverage = result.reactions_test_result.reactions_audio_errors_average,
                                    reactionsVisualErrorsType = EstimateType.entries.toTypedArray()
                                        .find {
                                            it.name == result.reactions_test_result.reactions_visual_errors_type
                                        } ?: EstimateType.UNKNOWN,
                                    reactionsAudioErrorsType = EstimateType.entries.toTypedArray()
                                        .find {
                                            it.name == result.reactions_test_result.reactions_audio_errors_type
                                        } ?: EstimateType.UNKNOWN,
                                    reactionAudioDiffAvg = result.reactions_test_result.reactions_audio_diff_avg,
                                    reactionVisualDiffAvg = result.reactions_test_result.reactions_visual_diff_avg,
                                    reactionAudioDiffStd = result.reactions_test_result.reactions_audio_std_avg,
                                    reactionVisualDiffStd = result.reactions_test_result.reactions_visual_std_avg,
                                    date = result.reactions_test_result.date.orEmpty(),
                                )
                            } else null,
                            textAuditionResult = if (result.text_audition_test_result != null) {
                                DayTextAuditionTestResult(
                                    qualityRead = result.text_audition_test_result.quality_read,
                                    qualityRepeat = result.text_audition_test_result.quality_repeat,
                                    qualityReadAverage = result.text_audition_test_result.quality_read_average,
                                    qualityRepeatAverage = result.text_audition_test_result.quality_repeat_average,
                                    qualityReadType = EstimateType.entries.toTypedArray().find {
                                        it.name == result.text_audition_test_result.quality_read_type
                                    } ?: EstimateType.UNKNOWN,
                                    qualityRepeatType = EstimateType.entries.toTypedArray().find {
                                        it.name == result.text_audition_test_result.quality_repeat_type
                                    } ?: EstimateType.UNKNOWN,
                                    date = result.text_audition_test_result.date.orEmpty(),
                                )
                            } else null,
                            escalDaily = if (result.escal_daily_test_result != null) {
                                DayEscalDailyTestResult(
                                    performance = result.escal_daily_test_result.performance,
                                    performanceType = EstimateType.entries.toTypedArray().find {
                                        it.name == result.escal_daily_test_result.performance_type
                                    } ?: EstimateType.UNKNOWN,
                                    fatigue = result.escal_daily_test_result.fatigue,
                                    fatigueType = EstimateType.entries.toTypedArray().find {
                                        it.name == result.escal_daily_test_result.fatigue_type
                                    } ?: EstimateType.UNKNOWN,
                                    anxiety = result.escal_daily_test_result.anxiety,
                                    anxietyType = EstimateType.entries.toTypedArray().find {
                                        it.name == result.escal_daily_test_result.anxiety_type
                                    } ?: EstimateType.UNKNOWN,
                                    conflict = result.escal_daily_test_result.conflict,
                                    conflictType = EstimateType.entries.toTypedArray().find {
                                        it.name == result.escal_daily_test_result.conflict_type
                                    } ?: EstimateType.UNKNOWN,
                                    sanX = result.escal_daily_test_result.sanX,
                                    sanZ = result.escal_daily_test_result.sanZ,
                                    date = result.escal_daily_test_result.date.orEmpty(),
                                )
                            } else null,
                            dayDescription = result.day_description,
                            type = EstimateType.entries.toTypedArray().find { 
                                it.name == result.day_type
                            } ?: EstimateType.UNKNOWN,
                        )
                    } else {
                        null
                    }
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrends(): Result<Trends?> {
        return try {
            val result = apiRepository.getTrends()

            Result.success(
                result?.let {
                    Trends(
                        shtangeResult = result.shtange_test_result?.map { res ->
                            DayShtangeTestResult(
                                shtangeResultIndicator = res.shtange_result_indicator,
                                shtangeResultIndicatorAverage = res.shtange_test_result_indicator_average,
                                type = EstimateType.entries.toTypedArray().find {
                                    it.name == res.type
                                } ?: EstimateType.UNKNOWN,
                                date = res.date,
                            )
                        },
                        personalReport = result.personal_report?.map { res ->
                            DayPersonalReport(
                                performanceMeasure = res.personal_report_current,
                                performanceMeasureAverage = res.personal_report_current_average,
                                type = EstimateType.entries.toTypedArray().find {
                                    it.name == res.type
                                } ?: EstimateType.UNKNOWN,
                                date = res.date,
                            )
                        },
                        pulseMeasurement = result.pulse_measurement?.map { res ->
                            DayPulseMeasurementResult(
                                pulseAverage = res.pulseAverage,
                                pulseMax = res.pulseMax,
                                pulseMin = res.pulseMin,
                                type = EstimateType.entries.toTypedArray().find {
                                    it.name == res.type
                                } ?: EstimateType.UNKNOWN,
                                date = res.date,
                            )
                        },
                        rufieTestResult = result.rufie_test_result?.map { res ->
                            DayRufieTestResult(
                                rufieResultIndicator = res.rufie_result_indicator,
                                rufieResultIndicatorAverage = res.rufie_test_result_indicator_average,
                                type = EstimateType.entries.toTypedArray().find {
                                    it.name == res.type
                                } ?: EstimateType.UNKNOWN,
                                date = res.date,
                            )
                        },
                        strupTestResult = result.strup_test_result?.map { res ->
                            DayStrupTestResult(
                                strupResult = res.strup_result,
                                strupResultAverage = res.strup_test_result_average,
                                type = EstimateType.entries.toTypedArray().find {
                                    it.name == res.type
                                } ?: EstimateType.UNKNOWN,
                                date = res.date,
                            )
                        },
                        genchTestResult = result.gench_test_result?.map { res ->
                            DayGenchTestResult(
                                genchResultIndicator = res.gench_result_indicator,
                                genchResultIndicatorAverage = res.gench_test_result_indicator_average,
                                type = EstimateType.entries.toTypedArray().find {
                                    it.name == res.type
                                } ?: EstimateType.UNKNOWN,
                                date = res.date,
                            )
                        },
                        reactionsResult = result.reactions_test_result?.map { res ->
                            DayReactionsTestResult(
                                reactionsVisualErrors = res.reactions_visual_errors,
                                reactionsAudioErrors = res.reactions_audio_errors,
                                reactionsVisualErrorsAverage = res.reactions_visual_errors_average,
                                reactionsAudioErrorsAverage = res.reactions_audio_errors_average,
                                reactionsVisualErrorsType = EstimateType.entries.toTypedArray()
                                    .find {
                                        it.name == res.reactions_visual_errors_type
                                    } ?: EstimateType.UNKNOWN,
                                reactionsAudioErrorsType = EstimateType.entries.toTypedArray()
                                    .find {
                                        it.name == res.reactions_audio_errors_type
                                    } ?: EstimateType.UNKNOWN,
                                reactionAudioDiffAvg = res.reactions_audio_diff_avg,
                                reactionVisualDiffAvg = res.reactions_visual_diff_avg,
                                reactionAudioDiffStd = res.reactions_audio_std_avg,
                                reactionVisualDiffStd = res.reactions_visual_std_avg,
                                date = res.date.orEmpty(),
                            )
                        },
                        textAuditionResult = result.text_audition_test_result?.map { res ->
                            DayTextAuditionTestResult(
                                qualityRead = res.quality_read,
                                qualityRepeat = res.quality_repeat,
                                qualityReadAverage = res.quality_read_average,
                                qualityRepeatAverage = res.quality_repeat_average,
                                qualityReadType = EstimateType.entries.toTypedArray().find {
                                    it.name == res.quality_read_type
                                } ?: EstimateType.UNKNOWN,
                                qualityRepeatType = EstimateType.entries.toTypedArray().find {
                                    it.name == res.quality_repeat_type
                                } ?: EstimateType.UNKNOWN,
                                date = res.date.orEmpty(),
                            )
                        },
                        escalDaily = result.escal_daily_test_result?.map { res ->
                            DayEscalDailyTestResult(
                                performance = res.performance,
                                performanceType = EstimateType.entries.toTypedArray().find {
                                    it.name == res.performance_type
                                } ?: EstimateType.UNKNOWN,
                                fatigue = res.fatigue,
                                fatigueType = EstimateType.entries.toTypedArray().find {
                                    it.name == res.fatigue_type
                                } ?: EstimateType.UNKNOWN,
                                anxiety = res.anxiety,
                                anxietyType = EstimateType.entries.toTypedArray().find {
                                    it.name == res.anxiety_type
                                } ?: EstimateType.UNKNOWN,
                                conflict = res.conflict,
                                conflictType = EstimateType.entries.toTypedArray().find {
                                    it.name == res.conflict_type
                                } ?: EstimateType.UNKNOWN,
                                sanX = res.sanX,
                                sanZ = res.sanZ,
                                date = res.date.orEmpty(),
                            )
                        },
                        estimation = result.estimation_result?.map { res ->
                            DayEstimationTestResult(
                                estimation = res.estimation,
                                date = res.date,
                            )
                        }
                    )
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Int.toDaysComparison(): String = when (this) {
        0 -> "LOT_WORSE"
        1 -> "WORSE"
        2 -> "SAME"
        3 -> "BETTER"
        4 -> "LOT_BETTER"
        else -> "LOT_BETTER"
    }
}
