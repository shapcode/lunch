package com.shapcode.lunch.compose.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
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
            val context = LocalContext.current
            viewState.restaurant?.let { restaurant ->
                Text(restaurant.name, style = MaterialTheme.typography.h4)
                restaurant.address?.let { Text(it) }
                restaurant.phone?.let { Text(it) }
                restaurant.website?.let { website ->
                    Button(
                        onClick = { uriHandler.openUri(website) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.website))
                    }
                }
                Button(
                    onClick = {
                        context.startActivity(
                            ShareCompat.IntentBuilder(context)
                                .setType("text/plain")
                                .setText(restaurant.address)
                                .createChooserIntent()
                        )
                    },
                    modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(id = R.string.share))
                }
            }
        }
    }

}