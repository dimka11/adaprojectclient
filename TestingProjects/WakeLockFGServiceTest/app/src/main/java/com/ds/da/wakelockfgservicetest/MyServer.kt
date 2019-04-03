package com.ds.da.wakelockfgservicetest

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.LifecycleOwner



class MyServer: LifecycleObserver {
    var int = 0
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun connect() {
        //int = 1
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun disconnect(){
        int = 5
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        int = 3
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(source: LifecycleOwner, event: Lifecycle.Event) {

    }
}