package com.shapcode.lunch.core.api.model

data class LatLng(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
) {
    override fun toString(): String {
        return "$lat,$lng"
    }
}