package com.example.lab2.data.remote

import com.example.lab2.data.remote.dtos.CbrResponseDto
import retrofit2.http.GET

interface CbrApi {
    companion object {
        const val BASE_URL = "https://www.cbr-xml-daily.ru/"
    }

    @GET("daily_json.js")
    suspend fun getDailyRates() : CbrResponseDto
}