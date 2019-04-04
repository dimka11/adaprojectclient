package com.ds.da.wakelockfgservicetest

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

        owner.buttonFGSStart.setOnClickListener {
            Intent(owner, ExampleService::class.java).also { intent ->
                owner.startService(intent)
            }
        }
        owner.buttonFGServiceStop.setOnClickListener {
            Intent(owner, ExampleService::class.java).also { intent ->
                owner.stopService(intent)
            }
        }
        owner.buttonSAF.setOnClickListener {
            owner.saf()
        }
        owner.buttonRetrofit.setOnClickListener {
            NetworkService.getInstance()
                .getJSONApi()
                .getPostWithID(1)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        val post = response.body()

                        owner.textViewRetrofit.append(post?.id.toString() + "\n")
                        owner.textViewRetrofit.append(post?.userId.toString() + "\n")
                        owner.textViewRetrofit.append(post?.title!! + "\n")
                        owner.textViewRetrofit.append(post?.body!! + "\n")
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {

                        owner.textViewRetrofit.append("Error occurred while getting request!")
                        t.printStackTrace()
                    }
                })
        }
    }
}
