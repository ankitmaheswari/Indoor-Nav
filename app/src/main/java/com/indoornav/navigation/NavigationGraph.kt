package com.indoornav.navigation

import android.nfc.Tag
import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.indoornav.business.store.AddNewStoreScreen
import com.indoornav.business.store.StoreActionOptionsScreen
import com.indoornav.business.store.StoreAndFloorSelectionScreen
import com.indoornav.business.store.actions.AddProductScreen
import com.indoornav.business.store.actions.MapProductToTagScreen
import com.indoornav.business.store.actions.MapTagToRackScreen
import com.indoornav.ui.screens.FloorPlanScreen
import com.indoornav.ui.screens.HomeScreen
import com.indoornav.ui.screens.customerflow.CustomerStoreScreen
import com.indoornav.ui.screens.customerflow.LandingScreen

@Composable
fun NavigationGraph(
    storeDatabase: DatabaseReference,
    productDatabase: DatabaseReference,
    productPositionDatabase: DatabaseReference,
    tagMappingDatabase: DatabaseReference,
    tag: MutableState<Tag?>,
) {
    val navController = rememberNavController()

    val gson by remember {
        mutableStateOf(Gson())
    }

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
            val qrValue = it?.arguments?.getString(NavigationRoute.QR_DATA)
            Log.d("CustomerStoreScreen qrvalue", qrValue.toString())
            CustomerStoreScreen(navController, gson, storeDatabase, productDatabase, qrValue!!)
        }



        composable(
            route = NavigationRoute.CREATE_STORE,
            deepLinks = listOf(NavDeepLink(NavigationRoute.CREATE_STORE))
        ) {
            AddNewStoreScreen(navController, storeDatabase)
        }

        composable(
            route = NavigationRoute.SELECT_STORE,
            deepLinks = listOf(NavDeepLink(NavigationRoute.SELECT_STORE))
        ) {
            StoreAndFloorSelectionScreen(
                navController,
                gson,
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
            StoreActionOptionsScreen(navController, gson, storeDatabase, it.arguments?.getString("storeId")!!, it.arguments!!.getString("floorId")!!)
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
            MapTagToRackScreen(navController, tag, gson, storeDatabase, tagMappingDatabase, it.arguments?.getString("storeId")!!, it.arguments?.getString("floorId")!!)
        }
    }
}