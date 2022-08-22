package com.shapcode.lunch.shared.vm

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
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
class RestaurantDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val restaurantRepo: RestaurantRepo,
    private val favoriteRepo: FavoriteRepo,
) : ViewModel() {

    private val _viewState: MutableStateFlow<RestaurantDetailViewState> = MutableStateFlow(RestaurantDetailViewState(isLoading = true))
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("restaurantId")?.let { restaurantId ->
                val favorites = favoriteRepo.getAllFavorites().contains(restaurantId)
                _viewState.update { it.copy(isFavorite = favorites) }
                val restaurant = restaurantRepo.getRestaurantDetails(restaurantId)
                _viewState.update { it.copy(restaurant = restaurant, isLoading = false) }
            }
        }
    }

    fun setFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                favoriteRepo.addFavorite(id)
            } else {
                favoriteRepo.removeFavorite(id)
            }
            _viewState.update {
                it.copy(isFavorite = isFavorite)
            }
        }
    }

}

data class RestaurantDetailViewState(
    val restaurant: Restaurant? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
)