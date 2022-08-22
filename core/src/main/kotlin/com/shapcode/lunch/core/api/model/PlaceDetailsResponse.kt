package com.shapcode.lunch.core.api.model

data class PlaceDetailsResponse(
    val status: PlacesSearchStatus,
    val result: Place,
    val html_attributions: List<String> = emptyList()
)