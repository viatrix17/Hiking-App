package com.example.roadapp.network

import com.example.roadapp.network.ApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java

object RetrofitInstance {
    // 1. Tworzysz bazowy obiekt Retrofit (to co napisałeś)
    private val retrofit by lazy {
        Retrofit.Builder()
//            .baseUrl("https://jsonplaceholder.typicode.com/")
            .baseUrl("https://gist.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Tworzysz instancję swojego interfejsu ApiService
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}