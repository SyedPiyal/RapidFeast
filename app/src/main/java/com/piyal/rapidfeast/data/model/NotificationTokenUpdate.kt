package com.piyal.rapidfeast.data.model

import com.google.gson.annotations.SerializedName

data class NotificationTokenUpdate(
        @SerializedName("notificationToken")
        val notificationToken: String? = null,
        @SerializedName("id")
        val id: String
)