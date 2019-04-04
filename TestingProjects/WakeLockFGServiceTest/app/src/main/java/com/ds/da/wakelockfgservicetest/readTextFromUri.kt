package com.ds.da.wakelockfgservicetest

import android.net.Uri
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class readTextFromUri(val context: MainActivity, val uri: Uri) {
    @Throws(IOException::class)
    fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    operator fun invoke() {
        readTextFromUri(uri)
    }
}