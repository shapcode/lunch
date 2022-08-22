package com.shapcode.lunch.core.api.model

data class Place(
    val name: String,
    val rating: Double,
    val user_ratings_total: Long,
    val price_level: Int,
    val photos: List<PlacePhoto>?,
    val place_id: String,
    val geometry: Geometry,
    val website: String?,
    val formatted_address: String?,
    val formatted_phone_number: String?,
)