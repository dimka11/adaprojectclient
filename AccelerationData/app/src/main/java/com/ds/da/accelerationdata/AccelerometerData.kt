package com.ds.da.accelerationdata

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.os.Bundle
import android.content.Intent


class AccelerometerData(val ctx: Context) {
    val sensorManager = ctx.getSystemService(SENSOR_SERVICE) as SensorManager
    val sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    lateinit var sensorListener: SensorEventListener
    operator fun invoke(fileWriter: FileWriter) {
        sensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                toLog(event)
                sendMessageToActivity(AccData(event.values[0], event.values[1], event.values[2]), "accelerometer")
                fileWriter.makeStringFromData(event, false, true, "", getUnixTimestamp())
            }

            private fun toLog(event: SensorEvent?) {
                Log.v(
                    "Accelerometer", event?.values?.get(0).toString()
                            + event?.values?.get(1).toString()
                            + event?.values?.get(2).toString()
                )
            }

        }
        val sensorDelay = GetSharedPreferences.getIntValue("update_rate")
        sensorManager.registerListener(sensorListener, sensorAccelerometer, sensorDelay)
    }

    private fun sendMessageToActivity(sensorValues: AccData, msg: String) {
        val intent = Intent("AccelerometerDataUpdates")
        intent.putExtra("Status", msg)
        val bundle = Bundle()
        bundle.putParcelable("SensorEvent", sensorValues)
        intent.putExtra("Acceleration", bundle)
        ctx.sendBroadcast(intent)
    }

    fun getUnixTimestamp(): Long {
        //val unixTime = System.currentTimeMillis() / 1000L
        val unixTime = System.currentTimeMillis()
        return unixTime
    }

    fun unregisterListenter() {
        if (::sensorListener.isInitialized) {
            sensorManager.unregisterListener(sensorListener)
        }
    }

}