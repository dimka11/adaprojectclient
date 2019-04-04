package com.ds.da.wakelockfgservicetest

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.os.PowerManager
import kotlinx.android.synthetic.main.activity_main.*

class ButtonsControl : LifecycleObserver {
    lateinit var wl: PowerManager.WakeLock
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStartListener(owner: LifecycleOwner) {
        (owner as MainActivity).buttonWLStart.setOnClickListener {
            val pm = owner.getSystemService(Context.POWER_SERVICE) as PowerManager
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "my_app:wl_tag")
            wl.acquire()
        }
        owner.buttonWLStop.setOnClickListener {
            if (::wl.isInitialized && wl.isHeld) wl.release()
        }

        owner.buttonFGSStart.setOnClickListener { }
        owner.buttonFGServiceStop.setOnClickListener { }
    }
}
