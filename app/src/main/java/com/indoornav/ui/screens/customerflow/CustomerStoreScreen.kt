package com.indoornav.ui.screens.customerflow

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.indoornav.R
import com.indoornav.business.store.Coordinate
import com.indoornav.business.store.FloorPlan
import com.indoornav.business.store.Product
import com.indoornav.business.store.Store
import com.indoornav.model.CategoryTabData
import com.indoornav.navigation.NavigationRoute
import com.indoornav.util.StringUtil


data class QRResponse(val storeId: String, val floorId: String, val cord: Coordinate)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomerStoreScreen(
    navController: NavHostController,
    gson: Gson,
    storeDatabase: DatabaseReference,
    productDatabase: DatabaseReference,
    qrValue: String
) {
    val context = LocalContext.current
    var qrResponse by remember {
        mutableStateOf<QRResponse?>(null)
    }
    LaunchedEffect(key1 = qrValue) {
        if (qrValue.isNotEmpty()) {
            var qr = StringUtil.getBase64DecodedString(qrValue)!!
            try {
                qr = qr.substring(qr.indexOf(" ")).trim()
                Log.d("qr", qr)
                qrResponse =
                    gson.fromJson(qr, QRResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    var selectedProductId by remember {
        mutableStateOf<String?>(null)
    }
    var productList by remember {
        mutableStateOf(listOf<Product>())
    }
    var store by remember {
        mutableStateOf<Store?>(null)
    }
    var floor by remember {
        mutableStateOf<FloorPlan?>(null)
    }
    var selectedTab by remember {
        mutableIntStateOf(0)
    }

/*    val filteredProductList = remember(selectedTab, productList) {
        filteredOrdersByType(selectedTab, productList)
    }*/



    LaunchedEffect(key1 = qrResponse, block = {
        qrResponse?.let {
            storeDatabase.child(it.storeId).get().addOnSuccessListener {
                store = gson.fromJson<Store>(gson.toJson(it.value), Store::class.java)
                store?.let {
                    try {
                        floor = gson.fromJson(
                            gson.toJson(it.floorPlan?.get(qrResponse?.floorId ?: "") ?: "{}"),
                            FloorPlan::class.java
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


    })

    var loader by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = productDatabase, block = {
        loader = true
        productDatabase.get().addOnSuccessListener {
            val productListType = object : TypeToken<List<Product>>() {}.type
            productList = gson.fromJson<List<Product>>(
                gson.toJson(ArrayList((it.getValue() as HashMap<String, HashMap<*, *>>).values)),
                productListType
            )
            Log.d("CustomerStoreScreen", productList.toString())
            loader = false
        }.addOnFailureListener {
            Log.e("CustomerStoreScreen", "Error getting data", it)
            Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
            loader = false
        }.addOnCanceledListener {
            Log.e("CustomerStoreScreen", "Error getting data")
            Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
            loader = false
        }
    })



    Scaffold(
        topBar = {
        },
        bottomBar = {
            Footer {
                if (selectedProductId == null) {
                    return@Footer
                }
                navController.navigate(
                    NavigationRoute.FLOOR_PLAN.replace("{storeId}", qrResponse!!.storeId)
                        .replace("{floorId}", qrResponse!!.floorId)
                        .replace("{productId}", selectedProductId.toString())
                        .replace("{row}", qrResponse!!.cord.rowId.toString())
                        .replace("{column}", qrResponse!!.cord.colId.toString())
                )
            }
        }) { outerPadding ->
        Box(
            modifier = Modifier
                .padding(outerPadding)
                .fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
            ) {

                item {
                    // Content of the Column
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

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
                item {
                    ProgressDialog(loader, message = "Please wait...")
                }

                item {
                    Text(
                        text = "Choose the item to locate",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    CategoryHeaderTab(selectedTab){
                        selectedTab = it
                    }
                }
                if (selectedTab == 0){
                    items(productList, key = { item -> item.productId }) { product ->
                        StoreItemCard(product, {
                            selectedProductId = it
                        }) {
                            selectedProductId == it
                        }
                    }
                } else {
                    item {
                        NoItemFound()
                    }
                }



            }

            StoreHeaderCard(navController, store)
        }

    }
}

@Composable
private fun CategoryHeaderTab(
    selectedTabId : Int,
    onSelectedCategoryClick: (Int) -> Unit,
    ) {
    val tabs = getCategoryTabList()
    val selectedTab = tabs[selectedTabId]

    Column {
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tabs.map {
                item {
                    CategoryPillItem(
                        currentTab = it,
                        selectedTab = selectedTab,
                        onSelectedCategoryClick = { id ->
                            onSelectedCategoryClick(id)},
                    )
                }
            }
        }
    }

}

@Composable
fun CategoryPillItem(
    currentTab: CategoryTabData,
    selectedTab: CategoryTabData,
    onSelectedCategoryClick: (Int) -> Unit,
) {

    val borderColor =
        if (currentTab == selectedTab) Color(0xFF44A037) else Color(0xFFE3E3E3)
    Row(
        modifier = Modifier
            .padding(start = 4.dp)
            .clickable { onSelectedCategoryClick(currentTab.id) }
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(8.dp)
            )
            .background(
                Color.White,
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
    ) {
        Icon(
            painter = painterResource(id = currentTab.icon),
            modifier = Modifier
                .width(24.dp)
                .height(24.dp),
            tint = Color.Unspecified,
            contentDescription = null,
        )
        Text(
            text = currentTab.categoryName,
            modifier = Modifier.padding(start = 4.dp),
            color = Color.Black,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun StoreHeaderCard(navController: NavHostController, store: Store?) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        GenericTopBar {
            navController.popBackStack()
        }
        // PNG image as the background
        Image(
            painter = painterResource(id = R.drawable.background_green), // Replace with your image resource
            contentDescription = "Background Image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0.5f)
        )


    }

}

@Composable
private fun Footer(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(
                horizontal = 12.dp,
                vertical = 16.dp
            )
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = Color(0xff44A037),
                shape = RoundedCornerShape(8.dp)
            )

            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.direction),
            modifier = Modifier
                .padding(end = 4.dp)
                .height(20.dp)
                .width(20.dp),
            tint = Color.Unspecified,
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "Find this Item",
            color = Color.White,
            fontSize = 16.sp,
            maxLines = 1
        )
    }

}

@Composable
private fun StoreItemCard(
    product: Product,
    onProductSelected: (String) -> Unit,
    isSelected: (String) -> Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(id = R.drawable.burger),
                    contentDescription = null,
                    modifier = Modifier
                        .height(54.dp)
                        .width(54.dp)
                )
                Column(Modifier.padding(start = 16.dp)) {
                    Text(text = product.name)
                    Row {
                        Text(
                            text = getPrice(product.priceInPaisa),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = getPrice(product.mrpInPaisa),
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                }
            }
            Box(Modifier.fillMaxWidth()) {
                RadioButton(
                    selected = isSelected(product.productId), onClick = {
                        onProductSelected(product.productId)
                    }, modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

        }
        Divider()
    }
}

private fun getCategoryTabList(): List<CategoryTabData> {
    return listOf(
        CategoryTabData(0,"Food", R.drawable.snack),
        CategoryTabData(1,"Grocery", R.drawable.apple),
        CategoryTabData(2,"Utility", R.drawable.utility),
        CategoryTabData(3,"Electronics", R.drawable.headphones),
    )
}

@Composable
fun ProgressDialog(isShowing: Boolean, message: String) {
    if (isShowing) {
        Dialog(onDismissRequest = { /* Dismiss the dialog */ }) {
            Surface(
                modifier = Modifier.width(280.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = message)
                }
            }
        }
    }
}

private fun getDrawableByProductName(name : String) : Int{

    return when(name.lowercase()){
        "kurkure" -> R.drawable.kurkure
        "lays" -> R.drawable.lays
        "wafer" -> R.drawable.wafers
        "caledar" -> R.drawable.noimage
        else -> R.drawable.noimage
    }
}

private fun getPrice(priceInPaisa : Int) : String{
    val priceInRupees = priceInPaisa/100
    return "₹$priceInRupees"
}

@Composable
private fun NoItemFound(){
Column(modifier = Modifier
    .fillMaxSize()
    .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Image(painter = painterResource(id = R.drawable.no_item_found), contentDescription = null, modifier = Modifier
        .height(50.dp)
        .width(50.dp))

    Text(text = "No Item Found for this Category", modifier = Modifier.padding(vertical = 16.dp), fontSize = 16.sp)

}
}


