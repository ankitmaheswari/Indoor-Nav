package com.indoornav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.indoornav.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        topBar = {
            TopAppBar(title = { Text(text = "Home") })
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(it)
        ) {

            Column(modifier = Modifier.fillMaxSize().align(Alignment.Center)) {

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Please select the flow",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        navController.navigate(NavigationRoute.FLOOR_PLAN.replace(
                            "{storeId}",
                            "d3fd16e4-9865-4b6b-b536-8020c9ae5a9b"
                        ).replace("{floorId}", "77d76590-e0da-406b-9818-52d938ceca02"))

                        //navController.navigate(NavigationRoute.LANDING_SCREEN)
                    }
                ) {
                    Text(text = "Customer Flow")
                }

                Button(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        navController.navigate(NavigationRoute.SELECT_STORE)
                    }
                ) {
                    Text(text = "Business Flow")
                }
            }
        }
    }
}