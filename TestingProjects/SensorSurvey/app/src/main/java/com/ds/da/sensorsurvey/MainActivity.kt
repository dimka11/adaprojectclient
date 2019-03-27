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
import android.util.FloatProperty
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

    //write file
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

        if (mSensorAccelerometer != null) {
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


                accelerationRemoveGravity(event)



                logger.logger(event)
                makeStringFromData(event)
                makeJSONFromData(event)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun accelerationRemoveGravity(event: SensorEvent) {
        //Acceleration gravity removed
        val gravity: Array<Float> = arrayOf(0.0f, 0.0f, 0.0f)
        val linear_acceleration: Array<Float> = arrayOf(0.0f, 0.0f, 0.0f)
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        val alpha: Float = 0.8f

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0]
        linear_acceleration[1] = event.values[1] - gravity[1]
        linear_acceleration[2] = event.values[2] - gravity[2]

        AccelerationGravityRemovedTextView.text = resources.getString(
            R.string.accelerationgr,
            event.values[0],
            event.values[1],
            event.values[2]
        )

        /*

                Note: You can use many different techniques to filter sensor data. The code sample above uses a simple filter constant (alpha) to create a low-pass filter. This filter constant is derived from a time constant (t), which is a rough representation of the latency that the filter adds to the sensor events, and the sensor's event delivery rate (dt). The code sample uses an alpha value of 0.8 for demonstration purposes. If you use this filtering method you may need to choose a different alpha value.
                 */
    }

    private fun makeStringFromData(event: SensorEvent) {
        val sb = java.lang.StringBuilder()
        sb.append(event.values[0]).append(",").append(event.values[1]).append(",").append(event.values[2]).append("\n")
        fileWriter.appendToFile(sb.toString())


    }

    private fun makeJSONFromData(event: SensorEvent) {
        val sb = java.lang.StringBuilder()
        val json_string = "{ x: " + event.values[0] + ", y: " + event.values[1] + ",z: " + event.values[2] + "}"
        val networkStream = NetworkStream("http://192.168.1.3:8080/")
        networkStream.sendData(json_string)
    }

}
