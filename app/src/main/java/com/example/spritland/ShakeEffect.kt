package com.example.spritland.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun ShakeEffect(
    enabled: Boolean = true,
    onShake: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    DisposableEffect(enabled) {
        if (!enabled || accelerometer == null) {
            onDispose { }
        } else {
            val detector = ShakeDetector(onShake)
            sensorManager.registerListener(detector, accelerometer, SensorManager.SENSOR_DELAY_UI)

            onDispose {
                sensorManager.unregisterListener(detector)
            }
        }
    }
}
