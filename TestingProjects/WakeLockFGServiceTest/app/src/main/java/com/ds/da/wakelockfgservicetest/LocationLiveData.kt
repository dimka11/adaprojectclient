package com.ds.da.wakelockfgservicetest

import android.arch.lifecycle.LiveData
import android.content.Context


class LocationLiveData(val context: Context) : LiveData<Int>() {
    val localService: Int = 0

    override fun onActive() {
        //localService.addListener(listener)
    }

    override fun onInactive() {
        //localService.removeListener(listener)
    }
}