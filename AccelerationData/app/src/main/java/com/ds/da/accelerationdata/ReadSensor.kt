package com.ds.da.accelerationdata

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.android.synthetic.main.activity_main.*

class ReadSensor(val context: Context, val mainActivity: MainActivity) : SensorEventListener {

    private var mSensorManager: SensorManager? = null
    private var mSensorAccelerometer: Sensor? = null


    fun run(registered: Boolean, updRate: Int) {
        mSensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (mSensorAccelerometer != null) {
            mSensorManager!!.registerListener(
                this, mSensorAccelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun changeUpdateRate(rate: String) {

        if (mSensorAccelerometer != null) {
            when (rate) {
                "SENSOR_DELAY_NORMAL" -> {
                    mSensorManager?.unregisterListener(this)
                    if (mSensorAccelerometer != null) {
                        mSensorManager!!.registerListener(
                            this, mSensorAccelerometer,
                            SensorManager.SENSOR_DELAY_NORMAL
                        )
                    }
                }
                "SENSOR_DELAY_FASTEST" -> {
                    mSensorManager?.unregisterListener(this)
                    if (mSensorAccelerometer != null) {
                        mSensorManager!!.registerListener(
                            this, mSensorAccelerometer,
                            SensorManager.SENSOR_DELAY_FASTEST
                        )
                    }
                }
                "SENSOR_DELAY_GAME" -> {
                    mSensorManager?.unregisterListener(this)
                    if (mSensorAccelerometer != null) {
                        mSensorManager!!.registerListener(
                            this, mSensorAccelerometer,
                            SensorManager.SENSOR_DELAY_GAME
                        )
                    }
                }

                "SENSOR_DELAY_UI" -> {
                    mSensorManager?.unregisterListener(this)
                    if (mSensorAccelerometer != null) {
                        mSensorManager!!.registerListener(
                            this, mSensorAccelerometer,
                            SensorManager.SENSOR_DELAY_UI
                        )
                    }
                }

                "5" -> {
                    mSensorManager?.unregisterListener(this)
                    if (mSensorAccelerometer != null) {
                        mSensorManager!!.registerListener(
                            this, mSensorAccelerometer,
                            200000
                        )
                    }
                }
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //not using
    }

    override fun onSensorChanged(event: SensorEvent) {
        mainActivity.updateAccelerationLabel(event)
        mainActivity.updateRateLabel()
        if (mainActivity.writeFileON) {
            mainActivity.fileWriter.makeStringFromData(event, mainActivity.isWriteLabel, mainActivity.isWriteTimeStamp, mainActivity.editTextWriteLabel.text.toString(), mainActivity.lastUnixTimestamp)
        }

        if (mainActivity.networkStreamON) {
            val sb = java.lang.StringBuilder()
            //val json_string = "{ x: " + event.values[0] + ", y: " + event.values[1] + ",z: " + event.values[2] + "}"
            val json_string = "{ \"x\": ${event.values[0]},\"y\": ${event.values[1]},\"z\":${event.values[2]}}"
            NetworkStream().execute(mainActivity.editTextURL.text.toString(), json_string)
        }
    }
}