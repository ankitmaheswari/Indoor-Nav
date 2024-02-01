package com.indoornav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    startX: Int,
                    startY: Int
) {
    LaunchedEffect(key1 = Unit, block = {
        floorPlanViewModel.getFloorPlan(storeId, floorId)
    })

    val screenState = floorPlanViewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Floor Plan of Store") })
        }
    ) {

        when(screenState.value) {
            ScreenState.SUCCESS -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(Color.White)
                ) {


                    val rows = floorPlanViewModel.getRows()
                    val columns = floorPlanViewModel.getColumns()

                    FloorPlanLayout(
                        rows = rows,
                        columns = columns,
                        hasShelf = { row, column ->
                            return@FloorPlanLayout floorPlanViewModel.hasShelf(row, column)
                        }
                    ) { row, column ->
                        return@FloorPlanLayout floorPlanViewModel.isInPath(row, column)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(onClick = {
                        floorPlanViewModel.getShortestPath(arrayOf(startX, startY), productId, storeId)
                    }) {
                        Text(text = "Fetch Path")
                    }
                }
            }

            ScreenState.ERROR -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color.White)) {
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
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color.White)) {
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