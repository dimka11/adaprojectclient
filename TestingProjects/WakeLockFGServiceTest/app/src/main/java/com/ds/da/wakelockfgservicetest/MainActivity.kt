package com.ds.da.wakelockfgservicetest

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import kotlinx.android.synthetic.main.activity_main.*
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.util.Log
import android.widget.Toast

private const val READ_REQUEST_CODE: Int = 42

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

    fun saf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            resultData?.data?.also { uri ->
                Log.i("SAF_DEBUG", "Uri: $uri")
                //showImage(uri)
                readTextFromUri(this, uri)()
            }
        }
    }

}