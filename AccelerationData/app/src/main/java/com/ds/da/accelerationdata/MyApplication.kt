package com.ds.da.accelerationdata

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MyApplication.context = applicationContext
    }
    companion object {
        @JvmStatic
        private var context: Context? = null

        @JvmStatic
        fun getAppContext(): Context? {
            return context
        }
    }
}