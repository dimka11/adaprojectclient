package com.ds.da.networkfiletest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference
import java.util.*
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SendDataButton.setOnClickListener {
            val callAPI = CallAPI()
            callAPI.execute("http://192.168.1.3:8080", "{param: value}")
        }

        retriveDataButton.setOnClickListener {
            val debugTextView = WeakReference(debugTextView)
            RetriveData(debugTextView).execute()
        }

        PostDataButton.setOnClickListener {
            postData().execute()
        }
        uploadFilebutton.setOnClickListener {
            uploadFile().execute()
        }

        timerTaskButton.setOnClickListener {
            val timerObj = Timer()
            val timerTaskObj = object : TimerTask() {
                override fun run() {
                    this@MainActivity.runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "timer task running!", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            timerObj.schedule(timerTaskObj, 0, 5000)
        }
    }
}
