package com.example.diplomapplication.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
    suspend fun postEscalResults(@Body escalResults: NWEscalResults)

    @GET("escal-results")
    suspend fun getEscalResults(): NWEscalResults

//    @POST("shtange-test")
}
