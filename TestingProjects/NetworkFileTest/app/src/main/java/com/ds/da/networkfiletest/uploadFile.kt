package com.ds.da.networkfiletest

import android.os.AsyncTask
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL


class uploadFile : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String?): String? {
        val fileName = "test.csv"
        val filepath = File(Environment.getExternalStorageDirectory().absolutePath + separator + fileName)
        val sourceFileUri: String = filepath.toString()
        try {
            var conn: HttpURLConnection?
            var dos: DataOutputStream?
            val lineEnd = "\r\n"
            val twoHyphens = "--"
            val boundary = "*****"
            var bytesRead: Int
            var bytesAvailable: Int
            var bufferSize: Int
            val buffer: ByteArray
            val maxBufferSize = 1 * 1024 * 1024
            val sourceFile = File(sourceFileUri)

            if (sourceFile.isFile) {

                try {
                    val upLoadServerUri = "http://192.168.1.3:8080/uploadFile"

                    // open a URL connection to the Servlet
                    val fileInputStream = FileInputStream(
                        sourceFile
                    )
                    val url = URL(upLoadServerUri)

                    // Open a HTTP connection to the URL
                    conn = url.openConnection() as HttpURLConnection
                    conn.doInput = true // Allow Inputs
                    conn.doOutput = true // Allow Outputs
                    conn.useCaches = false // Don't use a Cached Copy
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Connection", "Keep-Alive")
                    conn.setRequestProperty(
                        "ENCTYPE",
                        "multipart/form-data"
                    )
                    conn.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data;boundary=$boundary"
                    )
                    conn.setRequestProperty("bill", sourceFileUri)

                    dos = DataOutputStream(conn.outputStream)

                    dos.writeBytes(twoHyphens + boundary + lineEnd)
                    //dos.writeBytes(
                    //   "Content-Disposition: form-data; name=\"bill\";filename=\""
                    //          + sourceFileUri + "\"" + lineEnd
                   // )
                    dos.writeBytes(" Content-Disposition: form-data; name=\"csv\"; filename=\"test.csv   $lineEnd")
                    //dos.writeBytes(" Content-Length: 149456954;   $lineEnd")

                    //dos.writeBytes(lineEnd)

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available()

                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    buffer = ByteArray(bufferSize)

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize)
                        bytesAvailable = fileInputStream.available()
                        bufferSize = Math
                            .min(bytesAvailable, maxBufferSize)
                        bytesRead = fileInputStream.read(
                            buffer, 0,
                            bufferSize
                        )

                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd)
                    dos.writeBytes(
                        (twoHyphens + boundary + twoHyphens
                                + lineEnd)
                    )

                    // Responses from the server (code and message)
                    val serverResponseCode = conn.getResponseCode()
                    val serverResponseMessage = conn
                        .getResponseMessage()

                    if (serverResponseCode == 200) {

                        // messageText.setText(msg);
                        //Toast.makeText(ctx, "File Upload Complete.",
                        //      Toast.LENGTH_SHORT).show();

                        // recursiveDelete(mDirectory1);

                    }

                    // close the streams //
                    fileInputStream.close()
                    dos.flush()
                    dos.close()

                } catch (e: Exception) {

                    // dialog.dismiss();
                    e.printStackTrace()
                    throw java.lang.Exception()

                }

                // dialog.dismiss();

            } // End else block


        } catch (ex: Exception) {
            // dialog.dismiss();

            ex.printStackTrace()
        }

        return "Executed"

    }
}