package com.shapcode.lunch.core.model

import com.shapcode.lunch.core.api.model.LatLng

data class Restaurant(
    val id: String,
    val name: String,
    val rating: Double = 0.0,
    val reviews: Long = 0,
    val priceLevel: Int = 0,
    val photoUrl: String? = null,
    val location: LatLng,
    val phone: String? = null,
    val address: String? = null,
    val website: String? = null,
)