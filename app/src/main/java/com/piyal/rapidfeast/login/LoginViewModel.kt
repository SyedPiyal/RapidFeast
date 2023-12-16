package com.piyal.rapidfeast.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val performLogin = MutableLiveData<Resource<Response<UserPlaceModel>>>()
    val performLoginStatus: LiveData<Resource<Response<UserPlaceModel>>> get() = performLogin

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                performLogin.value = Resource.loading()
                val response = userRepository.login(loginRequest)
                performLogin.value = Resource.success(response)
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    performLogin.value = Resource.offlineError()
                } else {
                    performLogin.value = Resource.error(e)
                }
            }
        }
    }
}
