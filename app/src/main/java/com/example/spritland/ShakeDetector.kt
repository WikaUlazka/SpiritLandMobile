package com.example.spritland.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.sqrt

class ShakeDetector(
    private val onShake: () -> Unit
) : SensorEventListener {

    private var lastShakeTime: Long = 0

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // przybliżona siła (g-force)
        val gX = x / 9.81f
        val gY = y / 9.81f
        val gZ = z / 9.81f
        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        val now = System.currentTimeMillis()

        // próg potrząśnięcia (możesz zmienić na 2.0 / 2.7)
        if (gForce > 2.5f) {
            // anti-spam: max raz na 1 sek
            if (now - lastShakeTime > 1000) {
                lastShakeTime = now
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
