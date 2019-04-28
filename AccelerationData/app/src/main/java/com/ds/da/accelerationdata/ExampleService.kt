package com.ds.da.accelerationdata

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.widget.Toast


class ExampleService : Service() {
    private lateinit var accelerometerData: AccelerometerData
    private val fileWriter = FileWriter()
    lateinit var wakeLock: PowerManager.WakeLock

    companion object {
        val MSG_SAY_HELLO = 1
    }

    val mMessenger = Messenger(IncomingHandler())

    internal inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SAY_HELLO -> Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show()
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Toast.makeText(applicationContext, "binding", Toast.LENGTH_SHORT).show()
        return mMessenger.binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setNotificationChannel(): String {
        val NOTIFICATION_CHANNEL_ID = "com.example.simpleapp"
        val channelName = "My Background Service"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
        return NOTIFICATION_CHANNEL_ID
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        val NOTIFICATION_CHANNEL_ID = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNotificationChannel()
        } else {
            " "
        }
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MainActivity::class.java), 0)
        val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("title")
            .setContentText("text")
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        mainWorker()
        return START_STICKY
    }

    private fun mainWorker() {
        accelerometerData = AccelerometerData(applicationContext)
        accelerometerData(fileWriter)
        prepareToWriteData()

    }

    private fun prepareToWriteData() {
        fileWriter.createNewFile(true, false,"")
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AccData::MyWakelockTag").apply {
                acquire()
            }
        }
    }


    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        accelerometerData.unregisterListenter()
        if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
    }
}