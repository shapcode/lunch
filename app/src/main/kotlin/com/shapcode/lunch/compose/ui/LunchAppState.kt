package com.shapcode.lunch.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = listOf()
) {
    Search("search"),
    Detail("detail", listOf(navArgument("restaurantId") { type = NavType.StringType })),
}

class LunchAppState(
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope
) {

    fun navigateToRestaurantDetail(restaurantId: String) {
        coroutineScope.launch {
            navController.navigate("${Screen.Detail.route}/${restaurantId}")
        }
    }

}

@Composable
fun rememberLunchAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember {
    LunchAppState(navController, coroutineScope)
}