package com.indoornav.business.store

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.indoornav.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreActionOptionsScreen(navController: NavController, storeId: String, floorId: String) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        topBar = {
            TopAppBar(title = { Text(text = "Select Action") })
        }
    ) {
        Box(
            modifier = Modifier.padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {
                    val path = NavigationRoute.STORE_MAP_TAG_TO_RACK_SCREEN.replace(
                        "{storeId}",
                        storeId
                    ).replace("{floorId}", floorId)
                    navController.navigate(path)
                }, Modifier.height(56.dp)) {
                    Text("Map Tag to a Rack")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val path = NavigationRoute.STORE_ADD_PRODUCT_SCREEN.replace(
                        "{storeId}",
                        storeId
                    ).replace("{floorId}", floorId)
                    navController.navigate(path)
                }, Modifier.height(56.dp)) {
                    Text("Add Product")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val path = NavigationRoute.STORE_MAP_PRODUCT_TO_TAG_SCREEN.replace(
                        "{storeId}",
                        storeId
                    ).replace("{floorId}", floorId)
                    navController.navigate(path)
                }, Modifier.height(56.dp)) {
                    Text("Map Product To a Tag")
                }
            }
        }
    }

}