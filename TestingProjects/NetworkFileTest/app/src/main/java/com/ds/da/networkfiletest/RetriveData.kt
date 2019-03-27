package com.ds.da.networkfiletest

import android.os.AsyncTask
import android.widget.TextView
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import org.apache.commons.io.IOUtils
import java.lang.ref.WeakReference


class RetriveData(val debugTextView: WeakReference<TextView>) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String?): String? {
        val url = URL("http://www.android.com/")
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val bis = BufferedInputStream(urlConnection.inputStream)
            return  IOUtils.toString(bis, "UTF-8")
        }
        finally {
            urlConnection.disconnect()
        }
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        debugTextView.get()!!.text = result
    }
}