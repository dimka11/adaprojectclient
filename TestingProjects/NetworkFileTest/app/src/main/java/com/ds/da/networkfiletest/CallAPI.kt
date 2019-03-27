package com.ds.da.networkfiletest

import android.os.AsyncTask
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL



class CallAPI  : AsyncTask<String, String, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String? {
        val urlString = params[0] // URL to call
        val data = params[1] //data to post
        var out: OutputStream? = null

        try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            out = BufferedOutputStream(urlConnection.outputStream)
            urlConnection.doOutput = true
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("Content-Type", "application/json")

            BufferedWriter(OutputStreamWriter(out, "UTF-8")).use{
                it.write(data)
                it.flush()
            }
            out.close()

            urlConnection.connect()

        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }
}