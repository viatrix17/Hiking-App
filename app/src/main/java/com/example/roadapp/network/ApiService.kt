package com.example.roadapp.network

import com.example.roadapp.model.Post
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("posts")
    suspend fun getPostsByUser(
        @Query("userId") userId: Int
    ): List<Post>

    @GET("viatrix17/721e9293815e011950407b8d33adf1ae/raw/fd6947ebc1992527e4e6f2a34f3b0800a694b46f/routes.json")
    suspend fun getAllPosts(): List<Post>
}