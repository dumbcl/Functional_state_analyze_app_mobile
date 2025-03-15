package com.example.diplomapplication.data.network

import retrofit2.http.GET

interface ApiRepository {
    @GET("testStatus")
    fun getTests()
}
