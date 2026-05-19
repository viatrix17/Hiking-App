package com.example.roadapp.network

import com.example.roadapp.network.ApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
//            .baseUrl("https://jsonplaceholder.typicode.com/")
            .baseUrl("https://gist.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}