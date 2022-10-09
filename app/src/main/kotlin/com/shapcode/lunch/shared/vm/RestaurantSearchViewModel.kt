package com.shapcode.lunch.shared.vm

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.shapcode.lunch.core.api.model.LatLng
import com.shapcode.lunch.core.model.Restaurant
import com.shapcode.lunch.core.repo.FavoriteRepo
import com.shapcode.lunch.core.repo.RestaurantRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")
class RestaurantSearchViewModel @Inject constructor(
    private val locationProviderClient: FusedLocationProviderClient,
    private val restaurantRepo: RestaurantRepo,
    private val favoriteRepo: FavoriteRepo,
) : ViewModel() {

    enum class ViewType {
        LIST, MAP
    }

    var location: LatLng? = null
        private set

    private val _viewType = MutableStateFlow(ViewType.LIST)
    val viewType = _viewType.asStateFlow()

    private val _viewState: MutableStateFlow<RestaurantSearchViewState> = MutableStateFlow(RestaurantSearchViewState())
    val viewState = _viewState.asStateFlow()

    private val query: String? = null
    private var currentJob: Job? = null

    init {
        viewModelScope.launch {
            val favorites = favoriteRepo.getAllFavorites()
            _viewState.update { it.copy(favorites = favorites) }
            locationProviderClient.lastLocation.await()?.let {
                location = LatLng(it.latitude, it.longitude)
                searchRestaurants("")
            }
        }
    }

    fun setViewType(viewType: ViewType) {
        _viewType.value = viewType
    }

    fun searchRestaurants(query: String) {
        if (query == this.query) return
        val location = this.location ?: return

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            delay(500)
            val results = restaurantRepo.searchNearbyRestaurants(
                location = location,
                radius = 10_000,
                keyword = query
            )
            _viewState.update { it.copy(restaurants = results) }
        }
    }

    fun setFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val favorites = if (isFavorite) {
                favoriteRepo.addFavorite(id)
            } else {
                favoriteRepo.removeFavorite(id)
            }
            _viewState.update {
                it.copy(favorites = favorites)
            }
        }

    }

}

data class RestaurantSearchViewState(
    val restaurants: List<Restaurant> = emptyList(),
    val favorites: Set<String> = setOf()
)