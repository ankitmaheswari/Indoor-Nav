package com.indoornav.business.store.actions

import android.nfc.Tag
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.google.gson.reflect.TypeToken
import com.indoornav.business.store.Product
import com.indoornav.business.store.ProductPosition
import com.indoornav.business.store.Store
import com.indoornav.business.store.TagMapping
import com.indoornav.util.NfcUtil
import java.util.UUID

data class TagInfo(val tagId: String)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapProductToTagScreen(
    navController: NavHostController,
    tag: MutableState<Tag?>,
    productDatabase: DatabaseReference,
    productPlacementDatabase: DatabaseReference,
    storeId: String?,
    floorId: String?
) {

    val context = LocalContext.current
    var productDialogExpanded by remember { mutableStateOf(false) }
    val productList by remember {
        mutableStateOf(arrayListOf<Product>())
    }
    var selectedProduct: Product? by remember { mutableStateOf(null) }
    var productCount by remember { mutableStateOf(0) }
    var tagId by remember {
        mutableStateOf("")
    }
    val gson by remember {
        mutableStateOf(Gson())
    }

    LaunchedEffect(key1 = tag.value, block = {
        if (tag.value != null) {
            Log.d("LaunchedEffect", tag.value.toString())
            NfcUtil.readTag(tag.value!!, context) {
                tagId = gson.fromJson(it.toString(), TagInfo::class.java).tagId
            }
        }
    })
    DisposableEffect(effect = {
        onDispose {
            tag.value = null
        }
    }, key1 = Unit)

    LaunchedEffect(key1 = productDatabase, block = {

        productDatabase.get().addOnSuccessListener {
            val productListType = object : TypeToken<List<Product>>() {}.type
            productList.clear()
            productList.addAll(gson.fromJson<List<Product>>(gson.toJson(ArrayList((it.getValue() as HashMap<String, HashMap<*,*>>).values)), productListType))
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    })

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        topBar = {
            TopAppBar(title = { Text(text = "Map Product-to-tag") })
        }
    ) {
        Box(
            modifier = Modifier.padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Spacer(modifier = Modifier.height(100.dp))
                Text(text = "Please Select Product")
                ExposedDropdownMenuBox(
                    expanded = productDialogExpanded,
                    onExpandedChange = {
                        productDialogExpanded = !productDialogExpanded
                    }
                ) {
                    TextField(
                        value = selectedProduct?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productDialogExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = productDialogExpanded,
                        onDismissRequest = { productDialogExpanded = false }
                    ) {
                        productList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item?.name ?: "") },
                                onClick = {
                                    selectedProduct = item
                                    productDialogExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                if (tag.value == null) {
                    Text(text = "Tag Not Connected!")
                } else {
                    Text(text = "Tag Connected!!")
                }
                if (tagId.isNotEmpty()) {
                    Text(text = "Tag Id: $tagId")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Please Enter Product Count")
                OutlinedTextField(
                    value = productCount.toString(),
                    onValueChange = {
                        productCount = if (it.isEmpty()) 0 else it.toInt()
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (selectedProduct == null) {
                        Toast.makeText(context, "Please Select Product", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (tag.value == null) {
                        Toast.makeText(context, "Please Connect NFC Tag", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (productCount <= 0) {
                        Toast.makeText(context, "Please Enter Product Count", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val productPosition = ProductPosition(
                        productId = selectedProduct!!.productId,
                        tagId, productCount
                    )
                    productPlacementDatabase.child(productPosition.productId).setValue(productPosition)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Product mapped to rack Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        }.addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Some Error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }) {
                    Text(text = "Map Product to rack")
                }
            }
        }
    }
}