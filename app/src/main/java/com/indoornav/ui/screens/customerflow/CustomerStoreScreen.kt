package com.indoornav.ui.screens.customerflow

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
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
    var selectedProductId by remember {
        mutableStateOf<String?>(null)
    }

    val productList by remember {
        mutableStateOf(arrayListOf<Product>())
    }
    var store by remember {
        mutableStateOf<Store?>(null)
    }
    var floor by remember {
        mutableStateOf<FloorPlan?>(null)
    }

    LaunchedEffect(key1 = qrResponse.storeId, block = {
        storeDatabase.child(qrResponse.storeId).get().addOnSuccessListener {
            store = gson.fromJson<Store>(gson.toJson(it.value), Store::class.java)
            store?.let {
                try {
                    floor = gson.fromJson(
                        gson.toJson(it.floorPlan?.get(qrResponse.floorId) ?: "{}"),
                        FloorPlan::class.java
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    })

    LaunchedEffect(key1 = Unit, block = {
        productDatabase.get().addOnSuccessListener {
            val productListType = object : TypeToken<List<Product>>() {}.type
            productList.clear()
            productList.addAll(
                gson.fromJson<List<Product>>(
                    gson.toJson(ArrayList((it.getValue() as HashMap<String, HashMap<*, *>>).values)),
                    productListType
                )
            )
        }.addOnFailureListener {
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
            Footer {
                navController.popBackStack()
            }
        }) { outerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(outerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {

                StoreHeaderCard(navController, store)
                Text(text = "Choose the item to locate")
            }
            item {
                LazyRow() {

                }
            }

            items(productList.size) { item ->
            }

            productList.forEach { product ->
                item {
                    StoreItemCard(product){
                        selectedProductId = it
                    }
                }
            }


        }
    }
}

@Composable
private fun StoreHeaderCard(navController: NavHostController, store: Store?) {
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
            Text(
                text = store?.name ?: "Retail Store",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
private fun Footer(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(
                color = Color.Green,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(
                horizontal = 12.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.direction),
            modifier = Modifier.padding(end = 4.dp),
            tint = Color.Unspecified,
            contentDescription = null
        )
    }
    Text(
        text = "Find this Item",
        color = Color.White,
        fontSize = 16.sp,
        maxLines = 1
    )


}

@Composable
private fun StoreItemCard(product: Product, onProductSelected: (String) -> Unit) {
    Row {
        Image(painter = painterResource(id = R.drawable.burger), contentDescription = null)
        Column {
            Text(text = product.name)
            Text(text = "₹ ${product.mrpInPaisa}")
        }
        RadioButton(selected = false, onClick = {
            onProductSelected(product.productId)
        })
    }
}

