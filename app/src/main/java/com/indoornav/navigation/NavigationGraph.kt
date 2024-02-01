package com.indoornav.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavDeepLink
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.indoornav.ui.screens.FloorPlanScreen

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = NavigationRoute.HOME
    ) {
        composable(
            route = NavigationRoute.HOME,
            deepLinks = listOf(NavDeepLink(NavigationRoute.HOME))
        ) {
            FloorPlanScreen()
        }
    }
}