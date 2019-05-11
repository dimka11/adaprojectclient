package com.ds.da.accelerationdata

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

object GetSharedPreferences{
    @JvmStatic
    fun invoke(key: String): String {
        val settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext())!!
        return settings.getString(key, "")!!
    }
    fun getIntValue(key: String): Int {
        val settings = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext())!!
        return settings.getString(key, "20000").toInt()
    }

}