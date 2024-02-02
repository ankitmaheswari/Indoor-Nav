package com.indoornav.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.indoornav.R
import com.indoornav.vm.FloorPlanViewModel
import com.indoornav.vm.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloorPlanScreen(
    navController: NavController,
    floorPlanViewModel: FloorPlanViewModel,
    storeId: String,
    floorId: String,
    productId: String,
    startRow: Int,
    startColumn: Int
) {
    LaunchedEffect(key1 = Unit, block = {
        floorPlanViewModel.getFloorPlan(arrayOf(startRow, startColumn), storeId, floorId, productId)
    })

    val context = LocalContext.current
    val screenState = floorPlanViewModel.screenState.collectAsState()
    var showDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Floor Plan of Store") })
        }
    ) { paddingValues ->

        when (screenState.value) {
            ScreenState.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White)
                ) {



                    val rows = floorPlanViewModel.getRows()
                    val columns = floorPlanViewModel.getColumns()

                    item {
                        FloorPlanLayout(
                            rows = rows,
                            columns = columns,
                            hasShelf = { row, column ->
                                return@FloorPlanLayout floorPlanViewModel.hasShelf(row, column)
                            },
                            isInPath = { row, column ->
                                return@FloorPlanLayout floorPlanViewModel.isInPath(row, column)
                            },
                            isStart = { row, column ->
                                return@FloorPlanLayout row == startRow && column == startColumn
                            },
                            isDestination = { row, column ->
                                return@FloorPlanLayout floorPlanViewModel.isDestination(row, column)
                            },
                            getLabel = { row, column ->
                                return@FloorPlanLayout floorPlanViewModel.getLabel(row, column)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Follow the blue path to find your item",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        val productDetails =
                            floorPlanViewModel.getProductDetails(productId, storeId)
                        productDetails?.let { product ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Product Details:",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Product Name: ${product.name}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selling Price: ${product.priceInPaisa / 100}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "MRP: ${product.mrpInPaisa / 100}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                onClick = {
                                    showDialog = true
                                }
                            ) {
                                Text(text = "Mark Found")
                            }
                        }
                    }


                }
            }

            ScreenState.ERROR -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White)
                ) {
                    Text(
                        "Something went wrong while fetching floor plan",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp,
                        color = Color.Red
                    )
                }
            }

            else -> {
                ProgressAnimationDialog(true)
            }
        }

        DialogWithTextAndOkButton(showDialog, "Offer", "10% Off on Kurkure!! Why don't you buy?") {
            showDialog = false
            navController.popBackStack()
        }

    }

}

@Composable
fun ProgressAnimationDialog(isShowing: Boolean) {
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
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
                    LottieAnimation(composition, iterations = LottieConstants.IterateForever,)
                }
            }
        }
    }
}

@Composable
fun DialogWithTextAndOkButton(
    showDialog: Boolean,
    dialogTitle: String,
    dialogMessage: String,
    onOkClicked: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onOkClicked() },
            title = { Text(text = dialogTitle) },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        onOkClicked()
                    }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }
}