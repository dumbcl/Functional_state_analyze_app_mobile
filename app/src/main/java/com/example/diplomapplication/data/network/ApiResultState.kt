package com.example.diplomapplication.data.network

sealed class ApiResultState{
    class OnSuccess<T>(val data: T) : ApiResultState()
    class OnFailure(val errorMessage: String) : ApiResultState()
}
