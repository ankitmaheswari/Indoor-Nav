package com.indoornav.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.database.DatabaseReference
import com.indoornav.business.store.StoreAndFloorSelectionScreen
import com.indoornav.ui.screens.FloorPlanScreen
import com.indoornav.ui.screens.HomeScreen
import com.indoornav.ui.screens.customerflow.CustomerStoreScreen
import com.indoornav.ui.screens.customerflow.LandingScreen

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
            deepLinks = listOf(NavDeepLink(NavigationRoute.LANDING_SCREEN))
        ) {
            HomeScreen(navController)
        }
        composable(
            route = NavigationRoute.LANDING_SCREEN,
            deepLinks = listOf(NavDeepLink(NavigationRoute.LANDING_SCREEN))
        ) {
            LandingScreen(navController)
        }

        composable(
            route = NavigationRoute.FLOOR_PLAN,
            deepLinks = listOf(NavDeepLink(NavigationRoute.FLOOR_PLAN))
        ) {
            FloorPlanScreen()
        }

        composable(
            route = NavigationRoute.CUSTOMER_STORE_SCREEN,
            arguments = listOf(navArgument(NavigationRoute.QR_DATA) { type = NavType.StringType }),
            deepLinks = listOf(NavDeepLink(NavigationRoute.CUSTOMER_STORE_SCREEN))
        ) {
            CustomerStoreScreen(navController)
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