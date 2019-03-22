package com.ds.da.sensorsurvey

import android.os.Environment
import java.io.File.separator
import java.io.File
import android.os.Environment.getExternalStorageDirectory
import java.io.FileOutputStream
import java.nio.file.Files.exists

class FileWriter (val filename: String) {
    lateinit var file: File
    fun createNewFile(){
        file = File(Environment.getExternalStorageDirectory().toString() + File.separator + filename)
        file.createNewFile()
        if (file.exists()) {
            FileOutputStream(file, true).use {it.write("x, y, z \n".toByteArray())}
        }
    }

    fun appendToFile(string: String){
        if (file.exists()) {
            FileOutputStream(file, true).use {it.write(string.toByteArray())}
        }
    }
}