package com.piyal.rapidfeast.data.model

import com.google.gson.annotations.SerializedName


data class OrderStatusRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("orderStatus")
    val orderStatus: String
)