package com.indoornav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.indoornav.R

@Composable
fun FloorPlanLayout(rows: Int,
                    columns: Int,
                    hasShelf: (Int, Int) -> Boolean,
                    isInPath: (Int, Int) -> Boolean,
                    isStart: (Int, Int) -> Boolean,
                    isDestination: (Int, Int) -> Boolean
) {
    val exteriorPadding = 8.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val rowSize = getRowWidth(screenWidth, columns)
    val height = rowSize.times(rows) + exteriorPadding.times(2).value.toInt() + (rows).times(2)

    Box(
        modifier = Modifier
            .width(screenWidth.dp)
            .height(height.dp)
            .border(width = 1.dp, color = Color.Black)
            .background(Color.White)
            .padding(exteriorPadding)
    ) {
        LazyColumn {
            for (r in 0..<rows) {
                item {

                    Row {
                        for (c in 0..<columns) {

                            if (isInPath(r, c)) {
                                Box(
                                    modifier = Modifier
                                        .size(rowSize.dp)
                                        .background(Color.Yellow.copy(alpha = 0.4f))
                                        .border(width = 1.dp, color = Color.Blue)
                                ) {
                                    if (isStart(r, c)) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_start),
                                            contentDescription = "start",
                                            modifier = Modifier.align(Alignment.Center),
                                            tint = Color.Unspecified
                                        )
                                    } else if (isDestination(r, c)) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_destination),
                                            contentDescription = "start",
                                            modifier = Modifier.align(Alignment.Center),
                                            tint = Color.Unspecified
                                        )
                                    }
                                }
                            } else if (hasShelf(r, c)) {
                                Box(
                                    modifier = Modifier
                                        .size(rowSize.dp)
                                        .background(Color.Gray)
                                        .border(width = 1.dp, color = Color.Blue)
                                ) {
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(rowSize.dp)
                                        .background(Color.White)
                                        .border(width = 1.dp, color = Color.Blue)
                                ) {
                                    if (isStart(r, c)) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_start),
                                            contentDescription = "start",
                                            modifier = Modifier.align(Alignment.Center),
                                            tint = Color.Unspecified
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getRowWidth(width: Int, rows: Int = 1): Int {
    return (width - 2 * (rows-1)) / rows
}