package com.ds.da.wakelockfgservicetest

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.LiveData


class MyViewModel() : ViewModel() {
    var data = MutableLiveData<String>()
    fun getData_(): MutableLiveData<String> {
        return data
    }
    fun loadData() {
        // callback and post data
    }
}