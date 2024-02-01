package com.indoornav.navigation

import android.nfc.Tag
import androidx.compose.animation.EnterTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavDeepLink
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.database.DatabaseReference
import com.indoornav.business.store.StoreActionOptionsScreen
import com.indoornav.business.store.StoreAndFloorSelectionScreen
import com.indoornav.business.store.actions.AddProductScreen
import com.indoornav.business.store.actions.MapProductToTagScreen
import com.indoornav.business.store.actions.MapTagToRackScreen
import com.indoornav.ui.screens.FloorPlanScreen
import com.indoornav.ui.screens.HomeScreen

@Composable
fun NavigationGraph(
    storeDatabase: DatabaseReference,
    productDatabase: DatabaseReference,
    productPositionDatabase: DatabaseReference,
    tagMappingDatabase: DatabaseReference,
    tag: MutableState<Tag?>,
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
                navController,
                storeDatabase,
                productPositionDatabase,
                productDatabase,
                tagMappingDatabase
            )
        }

        composable(
            route = NavigationRoute.STORE_ACTION_SCREEN,
            deepLinks = listOf(NavDeepLink(NavigationRoute.STORE_ACTION_SCREEN)),
            arguments = listOf(
                navArgument("storeId") { defaultValue = "" },
                navArgument("floorId") { defaultValue = "" }
            ),
            enterTransition = { EnterTransition.None }
        ) {
            StoreActionOptionsScreen(navController, it.arguments?.getString("storeId")!!, it.arguments!!.getString("floorId")!!)
        }

        composable(
            route = NavigationRoute.STORE_MAP_PRODUCT_TO_TAG_SCREEN,
            deepLinks = listOf(NavDeepLink(NavigationRoute.STORE_MAP_PRODUCT_TO_TAG_SCREEN)),
            arguments = listOf(
                navArgument("storeId") { defaultValue = "" },
                navArgument("floorId") { defaultValue = "" }
            ),
            enterTransition = { EnterTransition.None }
        ) {
            MapProductToTagScreen(navController, tag, productDatabase, productPositionDatabase, it.arguments?.getString("storeId")!!, it.arguments?.getString("floorId")!!)
        }

        composable(
            route = NavigationRoute.STORE_ADD_PRODUCT_SCREEN,
            deepLinks = listOf(NavDeepLink(NavigationRoute.STORE_ADD_PRODUCT_SCREEN)),
            arguments = listOf(
                navArgument("storeId") { defaultValue = "" },
                navArgument("floorId") { defaultValue = "" }
            ),
            enterTransition = { EnterTransition.None }
        ) {
            AddProductScreen(navController, productDatabase, it.arguments?.getString("storeId")!!, it.arguments?.getString("floorId")!!)
        }

        composable(
            route = NavigationRoute.STORE_MAP_TAG_TO_RACK_SCREEN,
            deepLinks = listOf(NavDeepLink(NavigationRoute.STORE_MAP_TAG_TO_RACK_SCREEN)),
            arguments = listOf(
                navArgument("storeId") { defaultValue = "" },
                navArgument("floorId") { defaultValue = "" }
            ),
            enterTransition = { EnterTransition.None }
        ) {
            MapTagToRackScreen(navController, tag, tagMappingDatabase, it.arguments?.getString("storeId")!!, it.arguments?.getString("floorId")!!)
        }
    }
}