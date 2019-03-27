package com.ds.da.networkfiletest

import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import com.ds.da.networkfiletest.postData





class postData : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String?): String {
        val urlParameters: String = """ {"key" : "value"} """
        val postData = urlParameters.toByteArray(Charsets.UTF_8)
        val postDataLength = postData.size
        val request = "http://192.168.1.3:8080/"
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