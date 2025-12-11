package com.example.lab2.data.remote.dtos

import com.example.lab2.data.remote.dtos.ValuteDto
import com.google.gson.annotations.SerializedName

data class CbrResponseDto(
    @SerializedName("Date")
    val date: String,
    @SerializedName("Valute")
    val valute: Map<String, ValuteDto>
)