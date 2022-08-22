package com.shapcode.lunch.core.repo

import com.shapcode.lunch.core.api.model.LatLng
import com.shapcode.lunch.core.model.Restaurant

interface RestaurantRepo {
    suspend fun searchNearbyRestaurants(location: LatLng, radius: Long, keyword: String): List<Restaurant>
    suspend fun getRestaurantDetails(id: String): Restaurant
}