package com.indoornav.util

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

// Data class representing the x and y coordinates
data class Coordinate(val x: Int, val y: Int)
data class CoordinateWithShelf(val x: Int, val y: Int, val z: Int)

@Composable
fun CoordinateInput(onSave: (Coordinate) -> Unit) {
    var xState by remember { mutableStateOf(TextFieldValue()) }
    var yState by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = xState,
            onValueChange = { xState = it
                val x = xState.text.toIntOrNull() ?: 0
                val y = yState.text.toIntOrNull() ?: 0

                val coordinate = Coordinate(x, y)

                onSave(coordinate)},
            label = { Text("Row") },
            modifier = Modifier
                .fillMaxWidth()
        )

        OutlinedTextField(
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            value = yState,
            onValueChange = { yState = it
                val x = xState.text.toIntOrNull() ?: 0
                val y = yState.text.toIntOrNull() ?: 0

                val coordinate = Coordinate(x, y)

                onSave(coordinate)},
            label = { Text("Col") },
            modifier = Modifier
                .fillMaxWidth()
        )

    }
}

@Composable
fun CoordinateInputArray(
    coordinates: List<CoordinateWithShelf>,
    onAdd: (CoordinateWithShelf) -> Unit,
    onDelete: (CoordinateWithShelf) -> Unit
) {
    var xState by remember { mutableStateOf(TextFieldValue()) }
    var yState by remember { mutableStateOf(TextFieldValue()) }
    var zState by remember { mutableStateOf(TextFieldValue()) }

    Column {
        coordinates.forEach { coordinate ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Row: ${coordinate.x}, Col: ${coordinate.y}, Shelf: ${coordinate.z}", modifier = Modifier.weight(1f))

                IconButton(onClick = { onDelete(coordinate) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.Unspecified
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = xState,
                onValueChange = { xState = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Row") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            OutlinedTextField(
                value = yState,
                onValueChange = { yState = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Col") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = zState,
                onValueChange = { zState = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Shelf") },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                val x = xState.text.toIntOrNull() ?: 0
                val y = yState.text.toIntOrNull() ?: 0
                val z = zState.text.toIntOrNull() ?: 0

                val coordinate = CoordinateWithShelf(x, y, z)

                onAdd(coordinate)

                // Reset the input fields to empty
                xState = TextFieldValue()
                yState = TextFieldValue()
                zState = TextFieldValue()
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Unspecified
                )
            }
        }
    }
}