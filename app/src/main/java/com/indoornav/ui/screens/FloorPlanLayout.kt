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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indoornav.R
import com.indoornav.ui.theme.Border_Primary
import com.indoornav.ui.theme.Path_Fill
import com.indoornav.ui.theme.Shelf_Fill

@Composable
fun FloorPlanLayout(rows: Int,
                    columns: Int,
                    hasShelf: (Int, Int) -> Boolean,
                    isInPath: (Int, Int) -> Boolean,
                    isStart: (Int, Int) -> Boolean,
                    isDestination: (Int, Int) -> Boolean,
                    getLabel: (Int, Int) -> String?
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
                                        .background(Path_Fill)
                                        .border(width = 1.dp, color = Border_Primary)
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
                                        .background(Shelf_Fill)
                                        .border(width = 1.dp, color = Border_Primary), contentAlignment = Alignment.Center
                                ) {
                                    getLabel(r, c)?.let {
                                        Text(it, style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = Color(0xFF3AA02C), )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(rowSize.dp)
                                        .background(Color.White)
                                        .border(width = 1.dp, color = Border_Primary)
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

