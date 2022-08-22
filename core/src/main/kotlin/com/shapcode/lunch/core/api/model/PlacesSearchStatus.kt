package com.shapcode.lunch.core.api.model

enum class PlacesSearchStatus {
    OK,
    ZERO_RESULTS,
    INVALID_REQUEST,
    OVER_QUERY_LIMIT,
    REQUEST_DENIED,
    UNKNOWN_ERROR
}