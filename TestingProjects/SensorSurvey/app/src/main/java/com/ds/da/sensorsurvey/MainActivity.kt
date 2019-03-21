package com.ds.da.sensorsurvey

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.hardware.SensorManager
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private var mSensorManager: SensorManager? = null

    // Individual light and proximity sensors.
    private var mSensorProximity: Sensor? = null
    private var mSensorLight: Sensor? = null
    private var mSensorAccelerometer: Sensor? = null

    // TextViews to display current sensor values
    private var mTextSensorLight: TextView? = null
    private var mTextSensorProximity: TextView? = null

    //logging
    private var logger = Logger()
    var sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
    var currentDateandTime = sdf.format(Date())
    private var fileWriter = FileWriter(currentDateandTime.toString() + ".csv")

    override fun onCreate(savedInstanceState: Bundle?) {
        //create new file
        fileWriter.createNewFile()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = mSensorManager!!.getSensorList(Sensor.TYPE_ALL)

        val sensorText = StringBuilder()
        for (currentSensor in sensorList) {
            sensorText.append(currentSensor.name).append(
                System.getProperty("line.separator")
            )
        }

        val sensorTextView = findViewById<View>(R.id.sensor_list) as TextView
        sensorTextView.text = sensorText

        mTextSensorLight = findViewById(R.id.label_light) as TextView
        mTextSensorProximity = findViewById(R.id.label_proximity) as TextView

        mSensorProximity = mSensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        mSensorLight = mSensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)
        mSensorAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val sensor_error = resources.getString(R.string.error_no_sensor)

        if (mSensorLight == null) {
            mTextSensorLight!!.setText(sensor_error)
        }

        if (mSensorProximity == null) {
            mTextSensorProximity!!.setText(sensor_error)
        }

    }

    override fun onStart() {
        super.onStart()
        if (mSensorProximity != null) {
            mSensorManager!!.registerListener(
                this, mSensorProximity,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        if (mSensorLight != null) {
            mSensorManager!!.registerListener(
                this, mSensorLight,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        if (mSensorLight != null) {
            mSensorManager!!.registerListener(
                this, mSensorAccelerometer,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onStop() {
        super.onStop()
        mSensorManager!!.unregisterListener(this)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        val sensorType = event!!.sensor.type
        val currentValue = event.values[0]

        when (sensorType) {
            // Event came from the light sensor.
            Sensor.TYPE_LIGHT -> {
                // Handle light sensor
                mTextSensorLight!!.setText(getResources().getString(R.string.label_light, currentValue))

                testTextView.text = currentValue.toString()
                testTextView2.text = event.sensor.maximumRange.toString()

                //Background color
                if (currentValue > 100) {
                    window.decorView.setBackgroundColor(Color.CYAN)
                } else {
                    window.decorView.setBackgroundColor(Color.WHITE)
                }

            }
            //
            Sensor.TYPE_PROXIMITY -> {
                mTextSensorProximity!!.setText(getResources().getString(R.string.label_proximity, currentValue))
            }
            //
            Sensor.TYPE_ACCELEROMETER -> {
                label_acceleration.text =
                        resources.getString(
                            R.string.label_acceleration,
                            event.values[0],
                            event.values[1],
                            event.values[2]
                        )
                logger.logger(event)
                makeStringFromData(event)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun makeStringFromData(event: SensorEvent) {
        val sb = java.lang.StringBuilder()
        sb.append(event.values[0])
        sb.append(",")
        sb.append(event.values[1])
        sb.append(",")
        sb.append(event.values[2])
        sb.append("\n")
        fileWriter.appendToFile(sb.toString())
    }

}
