package com.shapcode.lunch.compose.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shapcode.lunch.R
import com.shapcode.lunch.compose.ui.screens.detail.RestaurantDetail
import com.shapcode.lunch.compose.ui.screens.search.RestaurantSearch
import com.shapcode.lunch.compose.ui.theme.AppTheme

@Composable
fun LunchApp() {
    AppTheme {
        val appState = rememberLunchAppState()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name_compose)) }
                )
            }
        ) { contentPadding ->
            NavHost(
                navController = appState.navController,
                startDestination = Screen.Search.route,
            ) {
                composable(Screen.Search.route) {
                    RestaurantSearch(
                        onRestaurantSelected = {
                            appState.navigateToRestaurantDetail(it.id)
                        },
                        modifier = Modifier.padding(contentPadding)
                    )
                }
                composable(
                    route ="${Screen.Detail.route}/{restaurantId}",
                    arguments = Screen.Detail.arguments
                ) {
                    RestaurantDetail(
                        modifier = Modifier.padding(contentPadding)
                    )
                }
            }
        }

    }

}