package com.shapcode.lunch.compose.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shapcode.lunch.R
import com.shapcode.lunch.core.api.model.LatLng
import com.shapcode.lunch.core.model.Restaurant
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
fun RestaurantList(
    items: List<Restaurant> = listOf(),
    favorites: Set<String> = setOf(),
    onItemClick: (Restaurant) -> Unit = {},
    onToggleFavorite: (Restaurant, Boolean) -> Unit = { _,_ -> },
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier,
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items, key = { it.id }) {
                val isFavorite = favorites.contains(it.id)
                RestaurantListItem(
                    restaurant = it,
                    isFavorite = isFavorite,
                    onItemClick = onItemClick,
                    onToggleFavorite = { restaurant -> onToggleFavorite(restaurant, !isFavorite) }
                )
            }
        }
        LaunchedEffect(items) {
            listState.animateScrollToItem(0)
        }
    }

}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun RestaurantListItem(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onItemClick: (Restaurant) -> Unit = {},
    onToggleFavorite: (Restaurant) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stateFavorite = stringResource(id = R.string.state_favorite)

    Card(
        backgroundColor = MaterialTheme.colors.background,
        elevation = 3.dp,
        onClick = { onItemClick(restaurant) },
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .semantics(mergeDescendants = true) {
                    if (isFavorite) {
                        stateDescription = stateFavorite
                    }
                    customActions = buildList {
                        if (isFavorite) {
                            add(CustomAccessibilityAction("Remove Favorite") {
                                onToggleFavorite(restaurant)
                                true
                            })
                        } else {
                            add(CustomAccessibilityAction("Add Favorite") {
                                onToggleFavorite(restaurant)
                                true
                            })
                        }

                    }
                }
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = restaurant.photoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1.0f)
            ) {
                Text(restaurant.name, style = MaterialTheme.typography.subtitle1)
                val accessibleRatingAndReviews = stringResource(
                    R.string.content_description_rating_reviews,
                    restaurant.rating, restaurant.reviews
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clearAndSetSemantics {
                        text = AnnotatedString(accessibleRatingAndReviews)
                    }
                ) {
                    repeat(5) {
                        val tint = if (it + 1 <= restaurant.rating.roundToInt()) {
                            Color(0xFFF4D24C)
                        } else {
                            Color(0xFFE6E6E6)
                        }
                        Icon(Icons.Default.Star, contentDescription = null, tint = tint)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("(${restaurant.reviews})")
                }
                Text("$".repeat(restaurant.priceLevel))
            }
            IconButton(
                onClick = { onToggleFavorite(restaurant) },
                modifier = Modifier.clearAndSetSemantics {  }
            ) {
                val (icon, tint) = if (isFavorite)
                    Pair(Icons.Default.Favorite, MaterialTheme.colors.primary)
                else
                    Pair(Icons.Default.FavoriteBorder, MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
                Icon(icon, contentDescription = "Favorite", tint = tint)
            }
        }
    }
}