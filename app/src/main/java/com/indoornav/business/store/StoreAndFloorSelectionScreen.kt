package com.indoornav.business.store

import android.graphics.drawable.Icon
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.navigation.NavController
import com.indoornav.R
import com.indoornav.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreAndFloorSelectionScreen(
    navController: NavController,
    gson: Gson,
    storeDatabase: DatabaseReference,
    productPositionDatabase: DatabaseReference,
    productDatabase: DatabaseReference,
    tagMappingDatabase: DatabaseReference
) {
    val context = LocalContext.current

    val storesList by remember {
        mutableStateOf(arrayListOf<Store>())
    }
    val floorsList by remember {
        mutableStateOf(arrayListOf<FloorPlan>())
    }
    var storeDialogExpanded by remember { mutableStateOf(false) }
    var floorDialogExpanded by remember { mutableStateOf(false) }
    var selectedStore: Store? by remember { mutableStateOf(null) }
    var selectedFloor: FloorPlan? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = storeDatabase, block = {

        storeDatabase.get().addOnSuccessListener {
            val storeListType = object : TypeToken<List<Store>>() {}.type
            storesList.clear()
            storesList.addAll(gson.fromJson<List<Store>>(gson.toJson(ArrayList((it.getValue() as HashMap<String, HashMap<*,*>>).values)), storeListType))
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    })

    LaunchedEffect(key1 = selectedStore, block = {
        selectedStore?.let {
            floorsList.clear()
            val floorPlanListType = object : TypeToken<List<FloorPlan>>() {}.type
            floorsList.addAll(gson.fromJson<List<FloorPlan>>(gson.toJson(it.floorPlan!!.values), floorPlanListType))
        }
    })


        Box(
            modifier = Modifier

                .fillMaxWidth(),
        ) {


            Box(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter), contentAlignment = Alignment.BottomCenter) {
                Text(text = "Add New Store", color = Color(0xFF2b9ccc), modifier = Modifier.padding(16.dp).clickable {
                    navController.navigate(NavigationRoute.CREATE_STORE)
                })
            }
            Column(Modifier.fillMaxWidth()) {
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
                        Text(text = "Inventory Management", style = TextStyle(fontSize = 32.sp))
                    }

                }
                Spacer(modifier = Modifier.height(50.dp))

                ExposedDropdownMenuBox(
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 64.dp),
                    expanded = storeDialogExpanded,
                    onExpandedChange = {
                        storeDialogExpanded = !storeDialogExpanded
                    }
                ) {
                    TextField(
                        value = selectedStore?.name ?: "Please Select Store",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = storeDialogExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = storeDialogExpanded,
                        onDismissRequest = { storeDialogExpanded = false }
                    ) {
                        storesList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item?.name ?: "") },
                                onClick = {
                                    selectedStore = item
                                    storeDialogExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 64.dp),
                    expanded = floorDialogExpanded,
                    onExpandedChange = {
                        floorDialogExpanded = !floorDialogExpanded
                    }
                ) {
                    TextField(
                        value = selectedFloor?.floorNumber?.toString() ?: "Please Select Floor",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = floorDialogExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = floorDialogExpanded,
                        onDismissRequest = { floorDialogExpanded = false }
                    ) {
                        floorsList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item?.floorNumber.toString() ?: "") },
                                onClick = {
                                    selectedFloor = item
                                    floorDialogExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))
                Button(modifier = Modifier.fillMaxWidth().padding(16.dp).height(64.dp).clip(RoundedCornerShape(16.dp)),
                    onClick = {
                    if (selectedStore?.storeId != null && selectedFloor?.floorId != null) {
                        val path = NavigationRoute.STORE_ACTION_SCREEN.replace(
                            "{storeId}",
                            selectedStore!!.storeId.toString()
                        ).replace("{floorId}", selectedFloor!!.floorId)
                        navController.navigate(path)
                    } else {
                        Toast.makeText(context, "Please Select Store and Floor", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Text(text = "Manage Inventory")
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