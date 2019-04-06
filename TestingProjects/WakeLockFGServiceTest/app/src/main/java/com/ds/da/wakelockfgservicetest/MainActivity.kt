package com.ds.da.wakelockfgservicetest

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import kotlinx.android.synthetic.main.activity_main.*
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.*
import android.view.View


private const val READ_REQUEST_CODE: Int = 42

class MainActivity : AppCompatActivity() {
    val myServer = MyServer()
    val stopWatch = BasicStopwatch(this)
    val buttonsControl = ButtonsControl()


    var mService: Messenger? = null
    var mBound: Boolean = false

     val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            mBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            mBound = false
        }
    }

    fun sayHello() {
        if (!mBound) return
        val msg = Message.obtain(null, ExampleService.MSG_SAY_HELLO, 0, 0)
        try {
            mService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

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


        registerReceiver(mMessageReceiver, IntentFilter("AccelerometerDataUpdates")
        )
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

    fun saf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                Log.i("SAF_DEBUG", "Uri: $uri")
                readTextFromUri(this, uri)()
            }
        }
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("Status")
            val b = intent.getBundleExtra("Acceleration")
            val accData = b.getParcelable<AccData>("SensorEvent")
            if (accData != null) {
                this@MainActivity.TextViewAccelerometer.text =
                    "accelearation x: ${accData.x} y: ${accData.y} z: ${accData.z} "
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }
}