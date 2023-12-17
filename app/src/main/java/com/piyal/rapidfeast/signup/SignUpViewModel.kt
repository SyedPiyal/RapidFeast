package com.piyal.rapidfeast.signup

import androidx.lifecycle.*
import com.piyal.rapidfeast.data.local.Resource
import com.piyal.rapidfeast.data.model.PlaceModel
import com.piyal.rapidfeast.data.model.Response
import com.piyal.rapidfeast.data.model.UpdateUserRequest
import com.piyal.rapidfeast.data.retrofit.PlaceRepository
import com.piyal.rapidfeast.data.retrofit.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _performFetchPlacesList = MutableLiveData<Resource<Response<List<PlaceModel>>>>()
    val performFetchPlacesList: LiveData<Resource<Response<List<PlaceModel>>>>
        get() = _performFetchPlacesList

    private var placesList: ArrayList<PlaceModel> = ArrayList()

    fun getPlaces() {
        viewModelScope.launch {
            try {
                _performFetchPlacesList.value = Resource.loading()
                val response = placeRepository.getPlaces()
                if (response.code == 1) {
                    if (!response.data.isNullOrEmpty()) {
                        placesList.clear()
                        placesList.addAll(response.data)
                        _performFetchPlacesList.value = Resource.success(response)
                    } else {
                        if (response.data != null) {
                            if (response.data.isEmpty()) {
                                _performFetchPlacesList.value = Resource.empty()
                            }
                        } else {
                            _performFetchPlacesList.value = Resource.error(null, message = "Something went wrong!")
                        }
                    }
                } else {
                    _performFetchPlacesList.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("fetch places list failed ${e.message}")
                if (e is UnknownHostException) {
                    _performFetchPlacesList.value = Resource.offlineError()
                } else {
                    _performFetchPlacesList.value = Resource.error(e)
                }
            }
        }
    }

    fun searchPlace(query: String?) {
        if (!query.isNullOrEmpty()) {
            val queryPlaceList = placesList.filter {
                it.name.toLowerCase().contains(query?.toLowerCase().toString())
            }
            _performFetchPlacesList.value = Resource.success(Response(1, queryPlaceList, ""))
        } else {
            _performFetchPlacesList.value = Resource.success(Response(1, placesList, ""))
        }
    }

    private val _performSignUp = MutableLiveData<Resource<Response<String>>>()
    val performSignUp: LiveData<Resource<Response<String>>>
        get() = _performSignUp

    fun signUp(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                _performSignUp.value = Resource.loading()
                val response = userRepository.updateUser(updateUserRequest)
                if (response.code == 1) {
                    if (response.data != null) {
                        _performSignUp.value = Resource.success(response)
                    } else {
                        _performSignUp.value = Resource.error(null, message = "Something went wrong")
                    }
                } else {
                    _performSignUp.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("Sign Up failed ${e.message}")
                if (e is UnknownHostException) {
                    _performSignUp.value = Resource.offlineError()
                } else {
                    _performSignUp.value = Resource.error(e)
                }
            }
        }
    }
}
