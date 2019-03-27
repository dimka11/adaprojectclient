package com.ds.da.accelerationdata

import android.hardware.SensorEvent
import android.os.AsyncTask
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NetworkStream: AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String): String {
        val urlParameters: String = params[1]
        val postData = urlParameters.toByteArray(Charsets.UTF_8)
        val postDataLength = postData.size
        val request = params[0]
        val url = URL(request)
        val conn = url.openConnection() as HttpURLConnection
        conn.setDoOutput(true)
        conn.setInstanceFollowRedirects(false)
        conn.setRequestMethod("POST")
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        conn.setRequestProperty("charset", "utf-8")
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength))
        conn.setUseCaches(false)

        DataOutputStream(conn.outputStream).use { wr ->

            wr.write(postData)
        }
        val reader = BufferedReader(InputStreamReader(conn.inputStream))
        var line: String? = null
        while (line != null) {
            line = reader.readLine()
            return line
        }
        reader.close()
        return ""
    }
}