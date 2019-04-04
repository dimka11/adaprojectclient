package com.ds.da.wakelockfgservicetest

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.widget.Toast

class PermissionRequester(val context: MainActivity) {
    var permResult = false
    fun request(): PermissionRequester {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        return this
    }

    fun result(requestCode: Int, permissions: Array<String>, grantResults: IntArray): PermissionRequester {
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                permResult = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }

        }
        return this
    }

    fun onGranted(function: () -> Unit): PermissionRequester {
        if (permResult) function()

        return this
    }

    fun onDenied(function: () -> Unit): PermissionRequester {
        if (!permResult) function()
        return this
    }
}