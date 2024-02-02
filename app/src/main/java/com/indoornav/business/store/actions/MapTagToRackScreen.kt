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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.indoornav.R
import com.indoornav.business.store.FloorPlan
import com.indoornav.business.store.Store
import com.indoornav.business.store.TagMapping
import com.indoornav.util.NfcUtil
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTagToRackScreen(
    navController: NavHostController,
    tag: MutableState<Tag?>,
    gson: Gson,
    storeDatabase: DatabaseReference,
    tagMappingDatabase: DatabaseReference,
    storeId: String,
    floorId: String
) {
    val context = LocalContext.current
    var rackId by remember { mutableStateOf("") }


    DisposableEffect(effect = {
        onDispose {
            tag.value = null
        }
    }, key1 = Unit)

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
                            Text(text = "Map Tag to Rack", style = TextStyle(fontSize = 24.sp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Go to particular rack where you want tag to map",
                                style = TextStyle(fontSize = 16.sp, color = Color(0xFF9D9D9D))
                            )
                        }

                    }
                    if (tag.value == null) {
                        Box(
                            modifier = Modifier.height(60.dp)
                                .fillMaxWidth()
                                .background(Color(0xFFFFFAE9)), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Tag Not Connected!")
                        }
                    } else {
                        Box(
                            modifier = Modifier.height(60.dp)
                                .fillMaxWidth()
                                .background(Color(0xFF44A037)), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Tag Connected!!")
                        }

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please Enter RackId",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    OutlinedTextField(
                        value = rackId.toString(),
                        onValueChange = {
                            rackId = it
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    Button(
                        onClick = {
                            if (rackId.isEmpty()) {
                                Toast.makeText(context, "Please Enter Tag Id", Toast.LENGTH_SHORT)
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
                            val tagMapping = TagMapping(
                                UUID.randomUUID().toString(),
                                storeId, floorId, rackId,
                                "NFC", "EMPTY"
                            )
                            NfcUtil.writeOnTag(
                                tag.value, """
                       { "tagId" : ${tagMapping.tagId}, "rackId" : ${tagMapping.rackId} }
                    """.trimIndent(), context
                            ) {
                                if (it) {
                                    tagMappingDatabase.child(tagMapping.tagId).setValue(tagMapping)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Tag mapped to rack Successfully!",
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
                                    tag.value = null
                                    Toast.makeText(
                                        context,
                                        "Failed to write on NFC Tag, Please retry!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Text(text = "Map tag to rack")
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