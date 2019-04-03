package com.ds.da.wakelockfgservicetest

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import kotlinx.android.synthetic.main.activity_main.*
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import android.content.pm.PackageManager




class MainActivity : AppCompatActivity() {

    val myServer = MyServer()
    val stopWatch = BasicStopwatch(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(myServer)
        lifecycle.addObserver(stopWatch)


        val liveData = DataController.getInstance().getData()
        val setLiveData = DataController.setData("Hello")
        liveData.observe(this, Observer<String> {
            textViewSW.text = it
        })

        val liveDataString = MutableLiveData<String>()
        val liveDataInteger = Transformations.map(liveDataString) { Integer.parseInt(it) }

        val model = ViewModelProviders.of(this).get(MyViewModel::class.java)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                        this@MainActivity,
                        "Permission denied to read your External storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
}
