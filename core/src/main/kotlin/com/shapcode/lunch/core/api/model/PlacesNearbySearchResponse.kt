package com.shapcode.lunch.core.api.model

data class PlacesNearbySearchResponse(
    val status: PlacesSearchStatus,
    val results: List<Place> = emptyList()
)