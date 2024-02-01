package com.indoornav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FloorPlanScreen() {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White)
        ) {

            Text(
                text = "Floor Plan of Store",
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            val rows = 30
            val columns = 7

            FloorPlanLayout(rows = rows, columns = columns) { row, column ->
                return@FloorPlanLayout (row != 0 && row != rows-1 && row % 2 == 1)
                        && (column != 0 && column != columns-1)
            }

        }
    }
}