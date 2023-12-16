package com.piyal.rapidfeast.data.model


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("oauthId")
    val oauthId: String
)