package com.indoornav.business.store.actions

import android.nfc.Tag
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.database.DatabaseReference
import com.indoornav.business.store.TagMapping
import com.indoornav.util.NfcUtil
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTagToRackScreen(
    navController: NavHostController,
    tag: MutableState<Tag?>,
    tagMappingDatabase: DatabaseReference,
    storeId: String,
    floorId: String
) {
    val context = LocalContext.current
    var rackId by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        topBar = {
            TopAppBar(title = { Text(text = "Map Tag-to-Rack") })
        }
    ) {
        Box(
            modifier = Modifier.padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column {
                if (tag.value == null) {
                    Text(text = "Tag Not Connected!")
                } else {
                    Text(text = "Tag Connected!!")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Please Enter RackId")
                OutlinedTextField(
                    value = rackId.toString(),
                    onValueChange = {
                        rackId = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(100.dp))

                Button(onClick = {
                    if (rackId.isEmpty()) {
                        Toast.makeText(context, "Please Enter Tag Id", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (tag.value == null) {
                        Toast.makeText(context, "Please Connect NFC Tag", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val tagMapping = TagMapping(
                        UUID.randomUUID().toString(),
                        storeId, floorId, rackId,
                        "NFC", "EMPTY"
                    )
                    NfcUtil.writeOnTag(tag.value, tagMapping.tagId, context) {
                        if (it) {
                            tagMappingDatabase.child(tagMapping.tagId).setValue(tagMapping)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Tag mapped tp rack Successfully!",
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
                        } else {
                            Toast.makeText(
                                context,
                                "Failed tow write on NFC Tag, Please retry!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }) {
                    Text(text = "Map tag to rack")
                }
            }
        }
    }
}