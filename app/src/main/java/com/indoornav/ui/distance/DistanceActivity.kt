package com.indoornav.ui.distance

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DistanceActivity : ComponentActivity(), SensorEventListener {
    private val TAG = "Distance_Tracker"
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var isCalculatingDistance = false
    private var distanceWalked = 0f
    private var lastAcceleration = 0f
    private var job: Job? = null
    private var distance: MutableStateFlow<Float> = MutableStateFlow(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        setContent {
            val distance = distance.collectAsState()
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        startDistanceCalculation()
                    }) {
                    Text(text = "Start")
                }
                Button(
                    onClick = {
                       stopDistanceCalculation()
                    }) {

                    Text(text = "Stop")
                }

                Text(text = "Distance Travelled : ${distance.value} meters")

            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && isCalculatingDistance) {
            val acceleration = event.values[0] + event.values[1] + event.values[2]
            val deltaAcceleration = acceleration - lastAcceleration
            distanceWalked += deltaAcceleration
            CoroutineScope(Dispatchers.Default).launch {
                distance.emit(distanceWalked)
            }
            lastAcceleration = acceleration
            Log.d(TAG, distanceWalked.toString())
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    private fun startDistanceCalculation() {
        isCalculatingDistance = true
        distanceWalked = 0f
        lastAcceleration = 0f
        job = CoroutineScope(Dispatchers.Default).launch {
            sensorManager?.registerListener(this@DistanceActivity, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun stopDistanceCalculation(): String {
        job?.cancel()
        isCalculatingDistance = false
        sensorManager?.unregisterListener(this)
        return "Distance Walked: $distanceWalked meters"
    }
}