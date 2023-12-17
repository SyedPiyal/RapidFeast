package com.piyal.rapidfeast.data.retrofit


import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(private val services: CustomApi) {

    suspend fun getShops(placeId: String) = services.getShops(placeId)
}
