package com.ds.da.wakelockfgservicetest

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.CountDownTimer
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class BasicStopwatch(val mainActivity: MainActivity) : LifecycleObserver {
    val counter: Int = 0

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun run() {
        var stopped = false
        mainActivity.buttonStart.setOnClickListener {
            stopped = false
            val counter = object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilDone: Long) {
                    Log.d("counter_label", "Counter text should be changed")
                    if (!stopped) {
                        mainActivity.textViewSW.text = "You have " + millisUntilDone + "ms"
                    }
                }

                override fun onFinish() {
                    mainActivity.textViewSW.text = "DONE!"

                }
            }.start()
        }
        mainActivity.buttonStop.setOnClickListener {

            mainActivity.textViewSW.text = "STOPPED"
            stopped = true
        }
    }

}