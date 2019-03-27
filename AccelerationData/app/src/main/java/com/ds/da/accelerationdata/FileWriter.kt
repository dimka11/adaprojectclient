package com.ds.da.accelerationdata

import android.hardware.SensorEvent
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileWriter {
    lateinit var file: File

    fun makeFileName(): String {
        var sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        var currentDateandTime = sdf.format(Date())
        return currentDateandTime.toString() + ".csv"
    }

    fun makeStringFromData(event: SensorEvent?) {
        val sb = java.lang.StringBuilder()
        if (event != null) {
            sb.append(event.values[0]).append(",").append(event.values[1]).append(",").append(event.values[2]).append("\n")
        }
        this.appendToFile(sb.toString())
    }

    fun createNewFile() {
        file = File(Environment.getExternalStorageDirectory().toString() + File.separator + makeFileName())
        file.createNewFile()
        if (file.exists()) {
            FileOutputStream(file, true).use { it.write("x, y, z \n".toByteArray()) }
        }
    }

    fun appendToFile(string: String) {
        if (file.exists()) {
            FileOutputStream(file, true).use { it.write(string.toByteArray()) }
        }
    }
}