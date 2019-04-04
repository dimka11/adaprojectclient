package com.ds.da.wakelockfgservicetest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import kotlinx.android.synthetic.main.activity_main.*
import android.arch.lifecycle.ViewModelProviders
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    val myServer = MyServer()
    val stopWatch = BasicStopwatch(this)
    val buttonsControl = ButtonsControl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(myServer)
        lifecycle.addObserver(stopWatch)
        lifecycle.addObserver(buttonsControl)

        val liveData = DataController.getInstance().getData()
        val setLiveData = DataController.setData("Hello")
        liveData.observe(this, Observer<String> {
            textViewSW.text = it
        })

        val liveDataString = MutableLiveData<String>()
        val liveDataInteger = Transformations.map(liveDataString) { Integer.parseInt(it) }

        val model = ViewModelProviders.of(this).get(MyViewModel::class.java)

        PermissionRequester(this).request()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        PermissionRequester(this).result(requestCode, permissions, grantResults).onGranted {
            Toast.makeText(
                this,
                "Permission granted to read your External storage",
                Toast.LENGTH_SHORT
            ).show()
        }.onDenied {
            Toast.makeText(
                this,
                "Permission denied to read your External storage",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}