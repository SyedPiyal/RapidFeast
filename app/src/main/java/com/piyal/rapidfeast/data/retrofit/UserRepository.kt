package com.piyal.rapidfeast.data.retrofit


import com.piyal.rapidfeast.data.model.LoginRequest
import com.piyal.rapidfeast.data.model.NotificationTokenUpdate
import com.piyal.rapidfeast.data.model.UpdateUserRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val services: CustomApi) {
    suspend fun login(loginRequest: LoginRequest) = services.login(loginRequest)
    suspend fun updateUser(updateUserRequest: UpdateUserRequest) = services.updateUser(updateUserRequest)
    suspend fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) = services.updateFcmToken(notificationTokenUpdate)
}
