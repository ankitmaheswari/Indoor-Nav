package com.indoornav.ui.screens.customerflow

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ComponentActivity
import androidx.navigation.NavHostController
import com.indoornav.R
import com.indoornav.navigation.NavigationRoute
import com.indoornav.ui.qrcode.QRCodeActivity
import com.indoornav.util.StringUtil

@RequiresApi(Build.VERSION_CODES.O)
@Composable
 fun LandingScreen(navController: NavHostController) {
    var qrData by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
        if (activityResult.resultCode == ComponentActivity.RESULT_OK) {
            qrData = activityResult.data?.getStringExtra("result")
            val base64 = StringUtil.getBase64EncodedString(qrData)
            val navigationRoute = NavigationRoute.CUSTOMER_STORE_SCREEN.replace("{QR_DATA}",base64 ?:"")
            navController.navigate(navigationRoute)
        }
    }

    Scaffold(
        topBar = {
            Column {

            }
        },
        bottomBar = {

        } ) { outerPadding ->
        Column(
            modifier = Modifier
                .padding(outerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFADE582)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth()) {
                GenericTopBar() {
                    navController.popBackStack()
                }
            }
            Text(text ="Introducing", fontSize = 16.sp, modifier = Modifier.padding(vertical = 4.dp) )
            Text(text = "Insta Spot",fontSize = 40.sp, modifier = Modifier.padding(vertical = 4.dp))
            Text(text = "Your instore assistant",fontSize = 16.sp, modifier = Modifier.padding(vertical = 4.dp), color = colorResource(R.color.purple_200))
            Image(painter = painterResource(id =R.drawable.phone_image), contentDescription = null, modifier = Modifier
                .padding(vertical = 8.dp)
                .height(360.dp)
                .width(341.dp))
            Icon(painter = painterResource(id = R.drawable.scan),
                tint = Color.Unspecified,
                contentDescription = null, modifier = Modifier.clickable {
                launcher.launch(createIntentSenderRequest(context))
            } )
        }


    }
}



private fun createIntentSenderRequest(context: Context): IntentSenderRequest {
    // Replace with your actual PendingIntent creation logic
    val intentSender = PendingIntent.getActivity(
        context,
        0,
        Intent(context, QRCodeActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    ).intentSender

    return IntentSenderRequest.Builder(intentSender).build()
}