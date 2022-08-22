package com.shapcode.lunch.core.api

import com.shapcode.lunch.core.api.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("nearbysearch/json")
    suspend fun nearbySearch(
        @Query("location") location: LatLng,
        @Query("keyword") keyword: String? = null,
        @Query("language") language: String? = null,
        @Query("maxprice") maxPrice: Int? = null,
        @Query("minprice") minPrice: Int? = null,
        @Query("radius") radius: Long? = null,
        @Query("type") type: String = "restaurant"
    ): PlacesNearbySearchResponse

    @GET("details/json")
    suspend fun placeDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String,
    ): PlaceDetailsResponse

}

