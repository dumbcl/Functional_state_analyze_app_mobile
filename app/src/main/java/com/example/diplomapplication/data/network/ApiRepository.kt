package com.example.diplomapplication.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiRepository {
    @GET("everything?apiKey=005edc5cc5024d65a3fd57133a0fefd4")
    suspend fun getNews(@Path("userId") userId: String)

    @GET("everything?q=bitcoin&apiKey=005edc5cc5024d65a3fd57133a0fefd4")
    suspend fun getNewss(@Path("userId") userId: String)
}
