package com.example.diplomapplication.data.network

import android.content.SharedPreferences
import com.example.diplomapplication.util.HEADER_AUTH
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        return if (!originalRequest.header(HEADER_AUTH).isNullOrEmpty()) {
            chain.proceed(originalRequest)
        } else {
            val token = getTokenFromSharedPreferences()
            val requestWithAuth = originalRequest.newBuilder()
                .addHeader(HEADER_AUTH, "Bearer $token")
                .build()
            chain.proceed(requestWithAuth)
        }
    }

    private fun getTokenFromSharedPreferences(): String {
        return sharedPreferences.getString("AUTH_TOKEN", "") ?: ""
    }
}
