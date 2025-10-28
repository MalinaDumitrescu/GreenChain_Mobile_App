package com.greenchain.core.network

import retrofit2.http.GET

data class PingDto(val message: String)

interface ApiService {
    @GET("ping") // replace with your real endpoint later
    suspend fun ping(): PingDto
}
