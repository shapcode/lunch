package com.shapcode.lunch.compose.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shapcode.lunch.R
import com.shapcode.lunch.core.model.Restaurant
import com.shapcode.lunch.shared.vm.RestaurantSearchViewModel

@Composable
fun RestaurantSearch(
    viewModel: RestaurantSearchViewModel = hiltViewModel(),
    onRestaurantSelected: (Restaurant) -> Unit = {},
    modifier: Modifier = Modifier,
) {

    val viewType by viewModel.viewType.collectAsState()
    val state by viewModel.viewState.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {
        TextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchRestaurants(query)
            },
            placeholder = { Text(stringResource(id = R.string.search_for_a_restaurant)) },
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = stringResource(id = R.string.search))
            },
            modifier = Modifier.fillMaxWidth()
        )
        Box {
            when (viewType) {
                RestaurantSearchViewModel.ViewType.LIST -> {
                    RestaurantList(
                        items = state.restaurants,
                        favorites = state.favorites,
                        onItemClick = onRestaurantSelected,
                        onToggleFavorite = { restaurant, isFavorite ->
                            viewModel.setFavorite(restaurant.id, isFavorite)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    Button(
                        onClick = { viewModel.setViewType(RestaurantSearchViewModel.ViewType.MAP) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = R.string.map))
                    }
                }
                RestaurantSearchViewModel.ViewType.MAP -> {
                    RestaurantMap(
                        items = state.restaurants,
                        onItemClick = onRestaurantSelected,
                        initialPosition = viewModel.location,
                        modifier = Modifier.fillMaxSize()
                    )
                    Button(
                        onClick = { viewModel.setViewType(RestaurantSearchViewModel.ViewType.LIST) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Icon(Icons.Default.List, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = R.string.list))
                    }
                }
            }
        }
    }
}