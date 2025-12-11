package com.example.lab2.data.remote.dtos

import com.google.gson.annotations.SerializedName

data class ValuteDto(
    @SerializedName("CharCode")
    val code: String,
    @SerializedName("Nominal")
    val nominal: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Value")
    val value: Double
)