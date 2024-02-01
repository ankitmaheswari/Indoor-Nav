package com.indoornav.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DatabaseReference
import com.indoornav.business.store.StoreAndFloorSelectionScreen
import com.indoornav.ui.screens.FloorPlanScreen
import com.indoornav.ui.screens.HomeScreen

@Composable
fun NavigationGraph(
    storeDatabase: DatabaseReference,
    productPositionDatabase: DatabaseReference,
    productDatabase: DatabaseReference,
    tagMappingDatabase: DatabaseReference,
) {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = NavigationRoute.HOME
    ) {
        composable(
            route = NavigationRoute.HOME,
            deepLinks = listOf(NavDeepLink(NavigationRoute.HOME))
        ) {
            HomeScreen(navController)
        }

        composable(
            route = NavigationRoute.FLOOR_PLAN,
            deepLinks = listOf(NavDeepLink(NavigationRoute.FLOOR_PLAN))
        ) {
            FloorPlanScreen()
        }

        composable(
            route = NavigationRoute.SELECT_STORE,
            deepLinks = listOf(NavDeepLink(NavigationRoute.SELECT_STORE))
        ) {
            StoreAndFloorSelectionScreen(
                storeDatabase,
                productPositionDatabase,
                productDatabase,
                tagMappingDatabase
            )
        }
    }
}