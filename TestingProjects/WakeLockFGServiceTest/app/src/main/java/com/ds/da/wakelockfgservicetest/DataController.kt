package com.ds.da.wakelockfgservicetest

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData



object DataController {
    private val liveData = MutableLiveData<String>()
    fun getInstance(): DataController {
        return this
    }
    fun getData(): LiveData<String> {
        return liveData
    }
    fun setData(ld: String) {
        liveData.value = ld
    }
}