package com.ds.da.sensorsurvey

import android.hardware.SensorEvent
import android.os.Build
import android.util.Log

class Logger {
    fun logger(event: SensorEvent) {
        val TAG = "Accelerometer"
        Log.v(TAG, "resolution" + event.sensor.resolution.toString())
        Log.v(TAG, "power " + event.sensor.power.toString())
        Log.v(TAG, "minDelay " + event.sensor.minDelay.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.v(TAG, "maxDelay " + event.sensor.maxDelay.toString())
        }
    }
}