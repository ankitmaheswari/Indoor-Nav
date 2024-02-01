package com.indoornav

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.indoornav.navigation.NavigationGraph
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import com.indoornav.business.store.PRODUCT
import com.indoornav.business.store.PRODUCT_POSITION
import com.indoornav.business.store.STORES
import com.indoornav.business.store.TAG_MAPPING
import com.indoornav.ui.theme.IndoorNavTheme

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private val tag = mutableStateOf<Tag?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val storeDatabase = Firebase.database.getReference(STORES)
        val productDatabase = Firebase.database.getReference(PRODUCT)
        val productPositionDatabase = Firebase.database.getReference(PRODUCT_POSITION)
        val tagMappingDatabase = Firebase.database.getReference(TAG_MAPPING)
        setContent {
            IndoorNavTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    NavigationGraph(
                        storeDatabase,
                        productDatabase,
                        productPositionDatabase,
                        tagMappingDatabase,
                        tag
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val ndefDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val techDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val nfcIntentFilter = arrayOf(techDetected, tagDetected, ndefDetected)
        val pendingIntent = PendingIntent.getActivity(this@MainActivity, 0, Intent(this@MainActivity, javaClass).addFlags(
            Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null)
    }

    override fun onPause() {
        super.onPause()

        nfcAdapter?.disableForegroundDispatch(this)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)?.let {
            tag.value = it
        } ?: run {
            tag.value = null
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