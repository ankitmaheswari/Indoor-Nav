package com.indoornav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import com.indoornav.business.store.PRODUCT
import com.indoornav.business.store.PRODUCT_POSITION
import com.indoornav.business.store.STORES
import com.indoornav.business.store.StoreAndFloorSelectionScreen
import com.indoornav.business.store.TAG_MAPPING
import com.indoornav.ui.theme.IndoorNavTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val storeDatabase = Firebase.database.getReference(STORES)
        val productDatabase = Firebase.database.getReference(PRODUCT)
        val productPositionDatabase = Firebase.database.getReference(PRODUCT_POSITION)
        val tagMappingDatabase = Firebase.database.getReference(TAG_MAPPING)
        setContent {
            IndoorNavTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StoreAndFloorSelectionScreen(
                        storeDatabase,
                        productPositionDatabase,
                        productDatabase,
                        tagMappingDatabase
                        )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IndoorNavTheme {
        Greeting("Android")
    }
}