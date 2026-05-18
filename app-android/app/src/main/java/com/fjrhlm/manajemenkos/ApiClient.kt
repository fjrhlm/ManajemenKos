package com.fjrhlm.manajemenkos

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Alamat IP 10.0.2.2 ini adalah localhost-nya Emulator Android
    private const val BASE_URL = "http://192.168.0.104:8000/api/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}