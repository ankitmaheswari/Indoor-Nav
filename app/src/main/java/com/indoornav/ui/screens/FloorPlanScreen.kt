package com.indoornav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indoornav.vm.FloorPlanViewModel
import com.indoornav.vm.ScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloorPlanScreen(floorPlanViewModel: FloorPlanViewModel,
                    storeId: String,
                    floorId: String,
                    productId: String,
                    startRow: Int,
                    startColumn: Int
) {
    LaunchedEffect(key1 = Unit, block = {
        floorPlanViewModel.getFloorPlan(storeId, floorId)
    })

    val screenState = floorPlanViewModel.screenState.collectAsState()
    val isPathFetched = floorPlanViewModel.isPathFetched.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Floor Plan of Store") })
        }
    ) { paddingValues ->

        when (screenState.value) {
            ScreenState.SUCCESS -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White)
                ) {


                    val rows = floorPlanViewModel.getRows()
                    val columns = floorPlanViewModel.getColumns()

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
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isPathFetched.value == false) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            onClick = {
                                // do nothing
                            },
                            enabled = false
                        ) {
                            Text(text = "Finding Path")
                        }
                    } else {

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            onClick = {
                                floorPlanViewModel.getShortestPath(
                                    arrayOf(
                                        startRow,
                                        startColumn
                                    ), productId, storeId
                                )
                            }
                        ) {
                            Text(text = "Find Path")
                        }
                    }

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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White)
                ) {
                    Text(
                        "Please wait, while we fetch the floor plan",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp,
                        color = Color.Blue
                    )
                }
            }
        }
    }

}