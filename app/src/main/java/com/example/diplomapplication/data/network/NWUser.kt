package com.example.diplomapplication.data.network

data class NWUserLoginRequest(
    val username: String?,
    val password: String?,
)

data class NWUserResponse(
    val access_token: String?,
)
