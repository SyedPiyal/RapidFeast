package com.piyal.rapidfeast.data.retrofit

import android.content.Context
import com.piyal.rapidfeast.data.local.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(val context: Context, val preferences: PreferencesHelper) :
        Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val whiteListedEndpoints = listOf(
                "/user/customer"
        )
        val request = if (!whiteListedEndpoints.contains(req.url().encodedPath())) {
            println("oauth_id testing 1"+preferences.oauthId)
            req.newBuilder()
                    .addHeader("oauth_id", preferences.oauthId)
                    .addHeader("id", preferences.userId.toString())
                    .addHeader("role", preferences.role)
                    .build()
        } else {
            println("oauth_id testing 2"+preferences.oauthId)
            req.newBuilder().build()
        }
        val response = chain.proceed(request)
        return response
    }
}