package com.shapcode.lunch.compose.ui.screens.search

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.Coil
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng as MapsLatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.shapcode.lunch.R
import com.shapcode.lunch.core.api.model.LatLng
import com.shapcode.lunch.core.model.Restaurant

@Composable
fun RestaurantMap(
    items: List<Restaurant> = listOf(),
    onItemClick: (Restaurant) -> Unit = {},
    initialPosition: LatLng? = null,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        initialPosition?.let {
            position = CameraPosition.fromLatLngZoom(
                MapsLatLng(initialPosition.lat, initialPosition.lng),
                11f
            )
        }
    }
    val mapStyleOptions = if (isSystemInDarkTheme()) {
        MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.map_style_dark)
    } else {
        null
    }
    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapStyleOptions = mapStyleOptions),
        modifier = modifier,
    ) {
        items.forEach { restaurant ->
            MarkerInfoWindow(
                state = MarkerState(
                    position = MapsLatLng(
                        restaurant.location.lat,
                        restaurant.location.lng
                    )
                ),
                onInfoWindowClick = { onItemClick(restaurant) },
            ) {
                RestaurantMapItem(restaurant = restaurant)
            }
        }
    }

    val context = LocalContext.current
    val padding = with(LocalDensity.current) {
        32.dp.roundToPx()
    }
    LaunchedEffect(items) {
        if (items.isNotEmpty()) {
            /* Determine the camera viewport which would include all markers at once. */
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(
                LatLngBounds(
                    MapsLatLng(
                        items.minBy { it.location.lat }.location.lat,
                        items.minBy { it.location.lng }.location.lng
                    ),
                    MapsLatLng(
                        items.maxBy { it.location.lat }.location.lat,
                        items.maxBy { it.location.lng }.location.lng
                    )
                ), padding
            ))
            /*
                Compose Maps implementation of InfoWindow doesn't update on recomposition,
                which means we have to prefetch the images (for better or worse) in order
                for the images to show when we click on the markers.
             */
            items.forEach {
                val request = ImageRequest.Builder(context)
                    .data(it.photoUrl)
                    .allowHardware(false)
                    .build()
                Coil.imageLoader(context).execute(request)
            }
        }
    }
}

@Composable
fun RestaurantMapItem(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        modifier = modifier.then(
            Modifier
                .padding(4.dp)
                .semantics(mergeDescendants = true) { }
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(restaurant.photoUrl)
                    .allowHardware(false)
                    .build(),
                contentDescription = "Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
            )
            Column {
                Text(restaurant.name, style = MaterialTheme.typography.subtitle1)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) {
                        val tint = if (it <= restaurant.rating) {
                            Color(0xFFF4D24C)
                        } else {
                            Color(0xFFE6E6E6)
                        }
                        Icon(Icons.Default.Star, contentDescription = null, tint = tint)
                    }
                    Text("(${restaurant.reviews})")
                }
                Text("$".repeat(restaurant.priceLevel))
            }
        }
    }
}
