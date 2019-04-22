package com.ds.da.accelerationdata

import android.content.Context
import android.graphics.Color
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorManager.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.support.annotation.RequiresPermission
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    val DEFAULT_SERVER = "http://192.168.1.3:8080/putData"
    var writeFileON: Boolean = false
    var networkStreamON: Boolean = false
    val fileWriter = FileWriter()
    lateinit var readSensor: ReadSensor
    lateinit var wakeLock: PowerManager.WakeLock
    var WLfile = false
    var WLNetwork = false

    override fun onCreate(savedInstanceState: Bundle?) {
        var fileWasCreated = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextURL.setText(DEFAULT_SERVER)
        buttonWriteFile.setOnClickListener {
            writeFileON = !writeFileON
            if (writeFileON) {
                if (!fileWasCreated) {
                    fileWriter.createNewFile()
                    fileWasCreated = true
                }
                WLfile = true
                acquireWakelock()
                buttonWriteFile.setBackgroundColor(Color.RED)
            } else {
                if (wakeLock.isHeld && !WLNetwork) releaseWakelock()
                buttonWriteFile.setBackgroundColor(Color.GRAY)
            }
        }

        buttonNetworkStream.setOnClickListener {
            networkStreamON = !networkStreamON
            if (networkStreamON) {
                acquireWakelock()
                WLNetwork = true
                buttonNetworkStream.setBackgroundColor(Color.RED)
            } else {
                if (wakeLock.isHeld && !WLfile) releaseWakelock()
                buttonNetworkStream.setBackgroundColor(
                    Color.GRAY
                )
            }
        }

        val readSensor = ReadSensor(this.applicationContext, this)
        readSensor.run(true, 0)
        try {
            spinnerUpdateRate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    itemSelected: View, selectedItemPosition: Int, selectedId: Long
                ) {
                    try {
                        readSensor.changeUpdateRate(resources.getStringArray(R.array.updateRates)[selectedItemPosition].toString())
                    } catch (e: kotlin.UninitializedPropertyAccessException) {
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        } catch (e: Throwable) {
        }
    }

    override fun onPause() {
        super.onPause()

    }

    fun updateAccelerationLabel(event: SensorEvent?) {
        if (event != null) {
            label_acceleration.text =
                resources.getString(
                    R.string.label_acceleration,
                    event.values[0],
                    event.values[1],
                    event.values[2]
                )
        }
    }

    fun acquireWakelock() {
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AccData::MyWakelockTag").apply {
                acquire()
            }
        }
    }

    fun releaseWakelock() {
        if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
    }


}
