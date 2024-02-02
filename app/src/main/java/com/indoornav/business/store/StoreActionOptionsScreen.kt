package com.indoornav.business.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.indoornav.R
import com.indoornav.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreActionOptionsScreen(
    navController: NavController,
    gson: Gson,
    storeDatabase: DatabaseReference,
    storeId: String,
    floorId: String
) {
    var store by remember {
        mutableStateOf<Store?>(null)
    }
    var floor by remember {
        mutableStateOf<FloorPlan?>(null)
    }
    LaunchedEffect(key1 = storeId, block = {
        storeDatabase.child(storeId).get().addOnSuccessListener {
            store = gson.fromJson<Store>(gson.toJson(it.value), Store::class.java)
            store?.let {
                try {
                    floor = gson.fromJson(
                        gson.toJson(it.floorPlan?.get(floorId) ?: "{}"),
                        FloorPlan::class.java
                    )
                } catch (e: Exception) {e.printStackTrace()}
            }
        }

    })

        Box(
            modifier = Modifier
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(color = Color(0xFFDEF2F7)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_store),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Unspecified
                        )
                        Text(text = ((store?.name?: "") + (if (floor?.floorNumber != null) (" " + floor!!.floorNumber.toString() + " floor") else "")) , style = TextStyle(fontSize = 24.sp))
                        Text(text = "Inventory Management", style = TextStyle(fontSize = 16.sp))
                    }

                }
                Spacer(modifier = Modifier.height(50.dp))

                Button(onClick = {
                    val path = NavigationRoute.STORE_MAP_TAG_TO_RACK_SCREEN.replace(
                        "{storeId}",
                        storeId
                    ).replace("{floorId}", floorId)
                    navController.navigate(path)
                }, Modifier.fillMaxWidth().padding(16.dp).height(64.dp).clip(RoundedCornerShape(16.dp)),) {
                    Text("Map Tag to a Rack")
                }

                Button(onClick = {
                    val path = NavigationRoute.STORE_ADD_PRODUCT_SCREEN.replace(
                        "{storeId}",
                        storeId
                    ).replace("{floorId}", floorId)
                    navController.navigate(path)
                }, Modifier.fillMaxWidth().padding(16.dp).height(64.dp).clip(RoundedCornerShape(16.dp)),) {
                    Text("Add Product")
                }

                Button(onClick = {
                    val path = NavigationRoute.STORE_MAP_PRODUCT_TO_TAG_SCREEN.replace(
                        "{storeId}",
                        storeId
                    ).replace("{floorId}", floorId)
                    navController.navigate(path)
                }, Modifier.fillMaxWidth().padding(16.dp).height(64.dp).clip(RoundedCornerShape(16.dp)),) {
                    Text("Map Product To a Tag")
                }
            }

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.left_arrow),
                        modifier = Modifier
                            .padding(end =12.dp )
                            .height(24.dp)
                            .width(24.dp).clickable {
                                navController.popBackStack()
                            }                 ,
                        tint = Color.Unspecified,
                        contentDescription = null
                    )

                }

            }

        }


}