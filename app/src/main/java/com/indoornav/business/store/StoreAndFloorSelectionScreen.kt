package com.indoornav.business.store

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.navigation.NavController
import com.indoornav.navigation.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreAndFloorSelectionScreen(
    navController: NavController,
    storeDatabase: DatabaseReference,
    productPositionDatabase: DatabaseReference,
    productDatabase: DatabaseReference,
    tagMappingDatabase: DatabaseReference
) {
    val context = LocalContext.current
    val gson by remember {
        mutableStateOf(Gson())
    }
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        topBar = {
            TopAppBar(title = { Text(text = "Select Store and Floor Number") })
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
                .fillMaxWidth()
        ) {
            Column {
                Spacer(modifier = Modifier.height(100.dp))
                Text(text = "Please Select Store")
                ExposedDropdownMenuBox(
                    expanded = storeDialogExpanded,
                    onExpandedChange = {
                        storeDialogExpanded = !storeDialogExpanded
                    }
                ) {
                    TextField(
                        value = selectedStore?.name ?: "",
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
                                    Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Please Select Floor")
                ExposedDropdownMenuBox(
                    expanded = floorDialogExpanded,
                    onExpandedChange = {
                        floorDialogExpanded = !floorDialogExpanded
                    }
                ) {
                    TextField(
                        value = selectedFloor?.floorNumber?.toString() ?: "",
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

                Spacer(modifier = Modifier.height(100.dp))
                Button(onClick = {
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
                    Text(text = "Go")
                }


            }
        }
    }
}