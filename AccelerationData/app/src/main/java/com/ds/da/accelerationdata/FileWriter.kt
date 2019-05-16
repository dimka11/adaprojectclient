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

    fun makeStringFromData(
        event: SensorEvent?,
        writeLabel: Boolean,
        writeTimeStamp: Boolean,
        editTextWriteLabel: String,
        lastUnixTimestamp: Long
    ) {
        val sb = java.lang.StringBuilder()
        if (event != null) {
            if (!writeLabel && !writeTimeStamp) {
                sb.append(event.values[0]).append(",").append(event.values[1]).append(",").append(event.values[2])
                    .append("\n")
            }
            if (writeLabel && writeTimeStamp) {
                sb.append(editTextWriteLabel).append(",").append(lastUnixTimestamp.toString()).append(",")
                sb.append(event.values[0]).append(",").append(event.values[1]).append(",").append(event.values[2])
                    .append("\n")
            }
            if (writeLabel && !writeTimeStamp) {
                sb.append(editTextWriteLabel).append(",")
                sb.append(event.values[0]).append(",").append(event.values[1]).append(",").append(event.values[2])
                    .append("\n")
            }
            if (!writeLabel && writeTimeStamp) {
                sb.append(lastUnixTimestamp.toString()).append(",")
                sb.append(event.values[0]).append(",").append(event.values[1]).append(",").append(event.values[2])
                    .append("\n")
            }

        }
        this.appendToFile(sb.toString())
    }

    fun createNewFile(
        writeTimeStamp: Boolean,
        writeLabel: Boolean,
        editTextWriteLabel: String
    ) {
        val checkDirectory = File(Environment.getExternalStorageDirectory().toString() + "/AccelerationData")
        if (!checkDirectory.isDirectory) {
            val newDirectory = File(Environment.getExternalStorageDirectory().toString() + "/AccelerationData/")
            newDirectory.mkdirs()
        }
        file =
            File(Environment.getExternalStorageDirectory().toString() + "/AccelerationData" + File.separator + makeFileName())

        file.createNewFile()
        if (file.exists()) {
            val basic = "x,y,z\n".toByteArray()
            val withTimeStamp = "timestamp, x, y, z \n".toByteArray()
            val withLabel = "label,x,y,z\n".toByteArray()
            val withLabelAndTimestamp = "$editTextWriteLabel, timestamp,x,y,z\n".toByteArray()

            if (!writeTimeStamp && !writeLabel) {
                FileOutputStream(file, true).use { it.write(basic) }
            }

            if (writeTimeStamp && writeLabel) {
                FileOutputStream(file, true).use { it.write(withLabelAndTimestamp) }
            }
            if (writeTimeStamp && !writeLabel) {
                FileOutputStream(file, true).use { it.write(withTimeStamp) }
            }
            if (!writeTimeStamp && writeLabel) {
                FileOutputStream(file, true).use { it.write(withLabel) }
            }
        }
    }

    fun appendToFile(string: String) {
        if (file.exists()) {
            FileOutputStream(file, true).use { it.write(string.toByteArray()) }
        }
    }
}