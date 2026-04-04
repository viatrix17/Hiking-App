package com.example.roadapp.model

import com.google.gson.annotations.SerializedName

data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    // Używamy @SerializedName, jeśli nazwa w JSON jest inna niż nasza zmienna
    // W tym przypadku w JSON jest "body", a my chcemy używać "description"
    @SerializedName("body")
    val description: String
)