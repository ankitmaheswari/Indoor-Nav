package com.indoornav.business.store.actions

import android.nfc.Tag
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.indoornav.R
import com.indoornav.business.store.Product
import com.indoornav.business.store.ProductPosition
import com.indoornav.business.store.Store
import com.indoornav.business.store.TagMapping
import com.indoornav.util.NfcUtil
import java.util.UUID

data class TagInfo(val tagId: String, val rackId: String)
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
    var rackId by remember {
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
                rackId = gson.fromJson(it.toString(), TagInfo::class.java).rackId
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


        Box(
            modifier = Modifier,
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
                                painter = painterResource(id = R.drawable.ic_tag),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Map Product to Tag", style = TextStyle(fontSize = 24.sp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Go to particular tag where you want to map the product",
                                style = TextStyle(fontSize = 16.sp, color = Color(0xFF9D9D9D))
                            )
                        }

                    }

                    if (tag.value == null) {
                        Box(
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth()
                                .background(Color(0xFFFFFAE9)), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Tag Not Connected!")
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth()
                                .background(Color(0xFF44A037)), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Tag Connected!!")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Please Select Product",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = productDialogExpanded,
                        onExpandedChange = {
                            productDialogExpanded = !productDialogExpanded
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 64.dp),
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


                    if (rackId.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .height(60.dp)
                                .fillMaxWidth()
                                .background(Color(0xFF44A037)), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Rack Id: $rackId")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Please Enter Product Count",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    OutlinedTextField(
                        value = productCount.toString(),
                        onValueChange = {
                            productCount = if (it.isEmpty()) 0 else it.toInt()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (selectedProduct == null) {
                                Toast.makeText(context, "Please Select Product", Toast.LENGTH_SHORT)
                                    .show()
                                return@Button
                            }
                            if (tag.value == null) {
                                Toast.makeText(
                                    context,
                                    "Please Connect NFC Tag",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            if (productCount <= 0) {
                                Toast.makeText(
                                    context,
                                    "Please Enter Product Count",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            val productPosition = ProductPosition(
                                productId = selectedProduct!!.productId,
                                tagId, productCount
                            )
                            productPlacementDatabase.child(productPosition.productId)
                                .setValue(productPosition)
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

                        }, modifier = Modifier.fillMaxWidth().padding(16.dp).height(64.dp).clip(
                            RoundedCornerShape(16.dp)
                        )
                    ) {
                        Text(text = "Map Product to rack")
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