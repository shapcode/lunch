package com.shapcode.lunch.core.repo.impl

import com.shapcode.lunch.core.BuildConfig
import com.shapcode.lunch.core.api.Api
import com.shapcode.lunch.core.api.model.LatLng
import com.shapcode.lunch.core.model.Restaurant
import com.shapcode.lunch.core.repo.RestaurantRepo
import com.shapcode.lunch.core.util.cache.Cache
import javax.inject.Inject
import kotlin.random.Random

class DefaultRestaurantRepo @Inject constructor(
    private val cache: Cache<List<Restaurant>>,
    private val api: Api,
) : RestaurantRepo {

    override suspend fun searchNearbyRestaurants(location: LatLng, radius: Long, keyword: String): List<Restaurant> {
        val cached = cache.get("$location:$keyword")
        if (cached != null) {
            return cached
        }

        val response = api.nearbySearch(location = location, radius = radius, keyword = keyword)
        val result = response.results.map {
            val photoUrl = it.photos?.firstOrNull()?.let {
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=240&photo_reference=${it.photo_reference}&key=${BuildConfig.PLACE_API_KEY}"
            }
            Restaurant(
                id = it.place_id,
                name = it.name,
                rating = it.rating,
                reviews = it.user_ratings_total,
                priceLevel = it.price_level,
                photoUrl = photoUrl,
                location = it.geometry.location,
                website = it.website,
                address = it.formatted_address,
                phone = it.formatted_phone_number,
            )
        }
        cache.put("$location:$keyword", result)
        return result
    }

    override suspend fun getRestaurantDetails(id: String): Restaurant {
        return api.placeDetails(
            id,
            listOf("place_id", "name", "rating", "user_ratings_total", "formatted_address", "formatted_phone_number", "website", "geometry", "photos").joinToString(",")
        ).result.let {
            val photoUrl = it.photos?.firstOrNull()?.let { photo ->
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=480&photo_reference=${photo.photo_reference}&key=${BuildConfig.PLACE_API_KEY}"
            }
            Restaurant(
                id = it.place_id,
                name = it.name,
                rating = it.rating,
                reviews = it.user_ratings_total,
                priceLevel = it.price_level,
                photoUrl = photoUrl,
                location = it.geometry.location,
                website = it.website,
                address = it.formatted_address,
                phone = it.formatted_phone_number,
            )
        }
    }

}

