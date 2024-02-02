package com.indoornav.business.store.actions

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.indoornav.R
import com.indoornav.business.store.FloorPlan
import com.indoornav.business.store.Product
import com.indoornav.business.store.Store
import com.indoornav.navigation.NavigationRoute
import java.lang.Exception
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

        Box(
            modifier = Modifier
        ) {
            LazyColumn {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(color = Color(0xFFDEF2F7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = Color.Unspecified
                            )
                            Text(
                                text = "Add Product to Inventory",
                                style = TextStyle(fontSize = 24.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fill in product details",
                                style = TextStyle(fontSize = 16.sp, color = Color(0xFF9D9D9D))
                            )
                        }

                    }
                    Text(
                        text = "Please Select Category",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = categoryDialogExpanded,
                        onExpandedChange = {
                            categoryDialogExpanded = !categoryDialogExpanded
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 64.dp),
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


                    Text(
                        text = "Please Enter Product Name",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    OutlinedTextField(
                        value = productName,
                        onValueChange = {
                            productName = it
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )


                    Text(
                        text = "Please Enter MRP",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    OutlinedTextField(
                        value = MRP.toString(),
                        onValueChange = {
                            MRP = if (it.isEmpty()) 0 else it.toInt()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Text(
                        text = "Please Enter Selling Price",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    OutlinedTextField(
                        value = sellingPrice.toString(),
                        onValueChange = {
                            sellingPrice = if (it.isEmpty()) 0 else it.toInt()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Button(
                        onClick = {
                            if (productName.isEmpty() || selectedCategory.isNullOrEmpty() || sellingPrice <= 0 || MRP <= 0) {
                                Toast.makeText(
                                    context,
                                    "Please Enter Product values properly!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            val product = Product(
                                UUID.randomUUID().toString(),
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
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Some Error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }, modifier = Modifier.fillMaxWidth().padding(16.dp).height(64.dp).clip(
                            RoundedCornerShape(16.dp)
                        )
                    ) {
                        Text(text = "Add Product")
                    }
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