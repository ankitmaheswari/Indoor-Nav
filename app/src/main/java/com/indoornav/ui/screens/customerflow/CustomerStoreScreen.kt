package com.indoornav.ui.screens.customerflow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.indoornav.R
import com.indoornav.navigation.NavigationRoute

@Composable
fun CustomerStoreScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val qrValue = navBackStackEntry?.arguments?.getString(NavigationRoute.QR_DATA)

    //todo fetch the data from firebase regarding that store and path


    Scaffold(
        topBar = {
            Column {
                GenericTopBar() {
                    navController.popBackStack()
                }
            }
        },
        bottomBar = {

        }) { outerPadding ->
        Column(
            modifier = Modifier
                .padding(outerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StoreHeaderCard(navController)

        }
    }
}

@Composable
private fun StoreHeaderCard(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // PNG image as the background
        Image(
            painter = painterResource(id = R.drawable.background_green), // Replace with your image resource
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f) // Adjust the alpha as needed
        )

        // Content of the Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            GenericTopBar {
                navController.popBackStack()

            }
            Image(
                painter = painterResource(id = R.drawable.store),
                contentDescription = null,
                modifier = Modifier
                    .height(45.dp)
                    .width(45.dp)
            )
            // Add your column content here
           // Text("Item 1", style = MaterialTheme.typography.h6, color = Color.White)
            //Text("Item 2", style = MaterialTheme.typography.body1, color = Color.White)
            // ... other items
        }
    }


}
