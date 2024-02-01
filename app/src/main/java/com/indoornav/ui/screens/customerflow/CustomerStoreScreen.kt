package com.indoornav.ui.screens.customerflow

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.indoornav.R
import com.indoornav.business.store.Coordinate
import com.indoornav.business.store.FloorPlan
import com.indoornav.business.store.Product
import com.indoornav.business.store.Store
import com.indoornav.navigation.NavigationRoute


data class QRResponse(val storeId: String, val floorId: String, val cord: Coordinate)
@Composable
fun CustomerStoreScreen(
    navController: NavHostController,
    gson: Gson,
    storeDatabase: DatabaseReference,
    productDatabase: DatabaseReference
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val qrValue = navBackStackEntry?.arguments?.getString(NavigationRoute.QR_DATA)
    val qrResponse = gson.fromJson(qrValue, QRResponse::class.java)
    val productList by remember {
        mutableStateOf(arrayListOf<Product>())
    }
    var store by remember {
        mutableStateOf<Store?>(null)
    }
    var floor by remember {
        mutableStateOf<FloorPlan?>(null)
    }
    //todo fetch the data from firebase regarding that store and path
    LaunchedEffect(key1 = qrResponse.storeId, block = {
        storeDatabase.child(qrResponse.storeId).get().addOnSuccessListener {
            store = gson.fromJson<Store>(gson.toJson(it.value), Store::class.java)
            store?.let {
                try {
                    floor = gson.fromJson(
                        gson.toJson(it.floorPlan?.get(qrResponse.floorId) ?: "{}"),
                        FloorPlan::class.java
                    )
                } catch (e: Exception) {e.printStackTrace()}
            }
        }

    })

    LaunchedEffect(key1 = Unit, block = {
        productDatabase.get().addOnSuccessListener {
            val productListType = object : TypeToken<List<Product>>() {}.type
            productList.clear()
            productList.addAll(gson.fromJson<List<Product>>(gson.toJson(ArrayList((it.getValue() as HashMap<String, HashMap<*,*>>).values)), productListType))
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    })



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
            Text("Welcome to", fontSize = 16.sp, color = Color.Black)
            Text("", fontSize = 24.sp , color = Color.Black, fontWeight = FontWeight.Bold)
            // ... other items
        }
    }


}
