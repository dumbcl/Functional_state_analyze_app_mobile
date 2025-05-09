package com.example.diplomapplication.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiRepository {
    @POST("auth/register")
    suspend fun register(@Body user: NWUserLoginRequest): Response<NWUserResponse>

    @POST("auth/login")
    suspend fun login(@Body user: NWUserLoginRequest): Response<NWUserResponse>

    @POST("pulse")
    suspend fun sendPulse(@Body heartRateRecords: List<NWHeartRateRecord>)

    @GET("pulse")
    suspend fun getPulse(
        @Query("from") from: String,
        @Query("to_") to: String,
    ): List<NWHeartRateRecord>

    @POST("escal-results")
    suspend fun postEscalResults(@Body results: NWEscalResults)

    @GET("escal-results")
    suspend fun getEscalResults(): NWEscalResults

    @POST("shtange-test")
    suspend fun postShtangeTestResult(
        @Body data: NWShtangeTestResult
    )

    @POST("gench-test")
    suspend fun postGenchTestResult(
        @Body data: NWGenchTestResult
    )

    @POST("rufie-test")
    suspend fun postRufieTestResult(
        @Body data: NWRufieTestResult
    )

    @POST("strup-test")
    suspend fun postStrupTestResult(
        @Body data: NWStrupTestResult
    )

    @POST("personal-report-test")
    suspend fun postPersonalReportTestResult(
        @Body data: NWPersonalReportTestResult
    )

    @POST("escal-daily-results")
    suspend fun postEscalDailyResults(
        @Body data: NWEscalDailyResults
    )

    @Multipart
    @POST("text-audition-result")
    suspend fun postTextAuditionResults(
        @Part("read_text_index") read_text_index: RequestBody,
        @Part("repeat_text_index") repeat_text_index: RequestBody,
        @Part read_text_file: MultipartBody.Part,
        @Part repeat_text_file: MultipartBody.Part
    )

    @GET("text-for-auditions")
    suspend fun getTextsForAuditions(): NWTextForAuditionResponse

    @POST("reactions-test")
    suspend fun postReactionTestResults(
        @Body data: NWReactionTestResults
    )

    @GET("available-tests")
    suspend fun getTests(): NWTestsDailyStatusResponse

    @GET("daily-test-results")
    suspend fun getResults(): List<NWDayDailyTestResult?>?
}
