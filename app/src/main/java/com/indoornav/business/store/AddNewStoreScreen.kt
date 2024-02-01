package com.indoornav.business.store

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.indoornav.util.Coordinate
import com.indoornav.util.CoordinateInput
import com.indoornav.util.CoordinateInputArray
import com.indoornav.util.CoordinateWithShelf
import java.util.Objects
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewStoreScreen(
    navController: NavHostController,
    storeDatabase: DatabaseReference,
) {
    val context = LocalContext.current
    val gson by remember {
        mutableStateOf(Gson())
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        topBar = {
            TopAppBar(title = { Text(text = "Create New Store") })
        }
    ) {
        Box(
            modifier = Modifier.padding(it),
            contentAlignment = Alignment.Center
        ) {
            var storeName by remember { mutableStateOf("") }
            var storeAddress by remember { mutableStateOf("") }
            var floorNumber by remember { mutableStateOf(0) }
            var rowsCount by remember { mutableStateOf(0) }
            var colsCount by remember { mutableStateOf(0) }
            var shelvesCount by remember { mutableStateOf(0) }
            var entryCords by remember { mutableStateOf(com.indoornav.util.Coordinate(0,0)) }
            var rackMappingCoordinatesShelf by remember { mutableStateOf(emptyList<CoordinateWithShelf>()) }



            LazyColumn {
                item {
                    Text(text = "Layout type: Grid")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Please Enter Store Name")
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = {
                            storeName = it
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Please Enter Store Complete Address")
                    OutlinedTextField(
                        value = storeAddress,
                        onValueChange = {
                            storeAddress = it
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Please Enter Floor Number")
                    OutlinedTextField(
                        value = floorNumber.toString(),
                        onValueChange = {
                            floorNumber = if (it.isEmpty()) 0 else it.toInt()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        Modifier
                            .background(Color.White, RoundedCornerShape(5.dp))
                            .padding(10.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Please Enter Total Rows")
                        OutlinedTextField(
                            value = rowsCount.toString(),
                            onValueChange = {
                                rowsCount = if (it.isEmpty()) 0 else it.toInt()
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Please Enter Total Cols")
                        OutlinedTextField(
                            value = colsCount.toString(),
                            onValueChange = {
                                colsCount = if (it.isEmpty()) 0 else it.toInt()
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Please Enter Total Shelves")
                        OutlinedTextField(
                            value = shelvesCount.toString(),
                            onValueChange = {
                                shelvesCount = if (it.isEmpty()) 0 else it.toInt()
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Please Enter Entry Location")
                        CoordinateInput { coordinate ->
                            entryCords = coordinate
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Please Enter Rack Mapping")
                        CoordinateInputArray( coordinates = rackMappingCoordinatesShelf,
                            onAdd = { coordinates ->
                                rackMappingCoordinatesShelf = rackMappingCoordinatesShelf + coordinates
                            },
                            onDelete = { coordinate ->
                                rackMappingCoordinatesShelf = rackMappingCoordinatesShelf.filter { it != coordinate }
                            }
                        )



                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { addNewStore(navController, storeDatabase, gson, storeName, storeAddress,
                                floorNumber, rowsCount, colsCount, shelvesCount,
                                entryCords, rackMappingCoordinatesShelf, context) }, Modifier.height(56.dp)) {
                            Text(text = "Save Store")
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

fun addNewStore(
    navController: NavHostController,
    storeDatabase: DatabaseReference,
    gson: Gson,
    storeName: String,
    storeAddress: String,
    floorNumber: Int,
    rowsCount: Int,
    colsCount: Int,
    shelvesCount: Int,
    entryCords: Coordinate,
    rackMappingCoordinatesShelf: List<CoordinateWithShelf>,
    context: Context
) {
    if (storeName.isEmpty()) {
        Toast.makeText(context, "Please Enter Store Name", Toast.LENGTH_SHORT).show()
        return
    }
    if (storeAddress.isEmpty()) {
        Toast.makeText(context, "Please Enter Store Address", Toast.LENGTH_SHORT).show()
        return
    }
    if (rowsCount <= 0) {
        Toast.makeText(context, "Please Enter Rows Count", Toast.LENGTH_SHORT).show()
        return
    }
    if (colsCount <= 0) {
        Toast.makeText(context, "Please Enter Columns Count", Toast.LENGTH_SHORT).show()
        return
    }
    if (shelvesCount <= 0) {
        Toast.makeText(context, "Please Enter Shelves Count", Toast.LENGTH_SHORT).show()
        return
    }
    if (rackMappingCoordinatesShelf.isEmpty()) {
        Toast.makeText(context, "Please Enter Rack Mapping", Toast.LENGTH_SHORT).show()
        return
    }
    val rackMapping = mutableMapOf<String,Rack>()
    for (cords in rackMappingCoordinatesShelf) {
        rackMapping[cords.x.toString() + cords.y.toString() + cords.z.toString()] = Rack(com.indoornav.business.store.Coordinate(cords.x.toInt(), cords.y.toInt()), cords.z.toInt())
    }
    val floorPlan = FloorPlan(UUID.randomUUID().toString(), floorNumber, layout = "GRID",
        rowsCount, colsCount, shelvesCount, com.indoornav.business.store.Coordinate(entryCords.x, entryCords.y), rackMapping
    )
    val map = HashMap<String, Object>()
    map[UUID.randomUUID().toString()] = gson.fromJson(gson.toJson(floorPlan), Object::class.java)
    val store = Store(UUID.randomUUID().toString(),
        storeName, storeAddress,
        floorPlan = map
    )
    storeDatabase.child(store.storeId.toString()).setValue(store).addOnSuccessListener {
        Toast.makeText(context, "Store Created Successfully", Toast.LENGTH_SHORT).show()
        navController.popBackStack()
    }.addOnCanceledListener {
        Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
    }
}