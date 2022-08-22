package com.shapcode.lunch.compose.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shapcode.lunch.R
import com.shapcode.lunch.shared.vm.RestaurantDetailViewModel

@Composable
fun RestaurantDetail(
    viewModel: RestaurantDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val viewState by viewModel.viewState.collectAsState()
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        if (viewState.isLoading) {
            CircularProgressIndicator()
        } else {
            viewState.restaurant?.let { restaurant ->
                Text(restaurant.name, style = MaterialTheme.typography.h4)
                restaurant.address?.let { Text(it) }
                restaurant.phone?.let { Text(it) }
                restaurant.website?.let { website ->
                    TextButton(onClick = { uriHandler.openUri(website) }) {
                        Text(stringResource(id = R.string.website))
                    }
                }
            }
        }
    }

}