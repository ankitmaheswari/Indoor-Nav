package com.indoornav.business.store.actions

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.indoornav.business.store.FloorPlan
import com.indoornav.business.store.Product
import com.indoornav.business.store.Store
import com.indoornav.navigation.NavigationRoute
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavHostController,
    productDatabase: DatabaseReference,
    storeId: String,
    floorId: String
) {
    val context = LocalContext.current
    var productName by remember { mutableStateOf("") }
    var MRP by remember { mutableStateOf(0) }
    var sellingPrice by remember { mutableStateOf(0) }
    var categoryDialogExpanded by remember { mutableStateOf(false) }
    var selectedCategory: String? by remember { mutableStateOf(null) }
    val categoryList by remember {
        mutableStateOf(arrayListOf<String>(
            "Utility", "Grocery", "Food", "Electronics"
            ))
    }
    Box(contentAlignment = Alignment.Center) {
        Column {
            Text(text = "Please Select Category")
            ExposedDropdownMenuBox(
                expanded = categoryDialogExpanded,
                onExpandedChange = {
                    categoryDialogExpanded = !categoryDialogExpanded
                }
            ) {
                TextField(
                    value = selectedCategory ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDialogExpanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = categoryDialogExpanded,
                    onDismissRequest = { categoryDialogExpanded = false }
                ) {
                    categoryList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item ?: "") },
                            onClick = {
                                selectedCategory = item
                                categoryDialogExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Please Enter Product Name")
            OutlinedTextField(
                value = productName,
                onValueChange = {
                    productName = it
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Please Enter MRP")
            OutlinedTextField(
                value = MRP.toString(),
                onValueChange = {
                    MRP = if (it.isEmpty()) 0 else it.toInt()
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Please Enter Selling Price")
            OutlinedTextField(
                value = sellingPrice.toString(),
                onValueChange = {
                    sellingPrice = if (it.isEmpty()) 0 else it.toInt()
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp))

            Button(onClick = {
                if (productName.isEmpty() || selectedCategory.isNullOrEmpty() || sellingPrice <= 0 || MRP <= 0) {
                    Toast.makeText(context, "Please Enter Product values properly!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val product = Product(UUID.randomUUID().toString(),
                    name = productName,
                    category = selectedCategory!!,
                    priceInPaisa = sellingPrice,
                    mrpInPaisa = MRP
                    )
                productDatabase
                    .child(product.productId)
                    .setValue(product)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Product Added Successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                    }
            }) {
                Text(text = "Add Product")
            }
        }
    }
}