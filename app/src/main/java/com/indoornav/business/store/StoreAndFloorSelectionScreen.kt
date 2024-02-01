package com.indoornav.business.store

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreAndFloorSelectionScreen(
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
    var expanded by remember { mutableStateOf(false) }
    var selectedStore: Store? by remember { mutableStateOf(null) }

    LaunchedEffect(key1 = storeDatabase, block = {

        storeDatabase.get().addOnSuccessListener {
            val storeListType = object : TypeToken<List<Store>>() {}.type
            storesList.clear()
            storesList.addAll(gson.fromJson<List<Store>>(gson.toJson(ArrayList((it.getValue() as HashMap<String, HashMap<*,*>>).values)), storeListType))
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedStore?.name ?: "Please Select Store",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                storesList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item?.name ?: "" ) },
                        onClick = {
                            selectedStore = item
                            expanded = false
                            Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}