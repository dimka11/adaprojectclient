package com.ds.da.accelerationdata

import android.content.*
import android.graphics.Color
import android.hardware.SensorEvent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import android.view.MenuItem
import android.preference.PreferenceManager
import android.net.Uri
import android.os.*
import android.view.WindowManager
import android.widget.Toast

private const val READ_REQUEST_CODE: Int = 42

class MainActivity : AppCompatActivity(), RealtimeUpdatesFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val owner = this

    val DEFAULT_SERVER = "http://192.168.1.3:8080/putData"
    var writeFileON: Boolean = false
    var networkStreamON: Boolean = false
    val fileWriter = FileWriter()
    lateinit var readSensor: ReadSensor
    lateinit var wakeLock: PowerManager.WakeLock
    var WLfile = false
    var WLNetwork = false
    var lastUnixTimestamp: Long = 1
    private var lastUpdateRate: Long = 1
    private val updateRateArray = arrayListOf<Long>()

    var isWriteTimeStamp = false
    var isWriteLabel = false

    override fun onCreate(savedInstanceState: Bundle?) {


        var fileWasCreated = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionRequester(this).request()

        editTextURL.setText(DEFAULT_SERVER)
        buttonWriteFile.setOnClickListener {
            writeFileON = !writeFileON
            if (writeFileON) {
                if (!fileWasCreated) {
                    fileWriter.createNewFile(isWriteTimeStamp, isWriteLabel, editTextWriteLabel.text.toString())
                    fileWasCreated = true
                }
                WLfile = true
                acquireWakelock()
                buttonWriteFile.setBackgroundColor(Color.RED)
            } else {
                if (wakeLock.isHeld && !WLNetwork) releaseWakelock()
                buttonWriteFile.setBackgroundColor(Color.GRAY)
            }
        }

        buttonNetworkStream.setOnClickListener {
            networkStreamON = !networkStreamON
            if (networkStreamON) {
                acquireWakelock()
                WLNetwork = true
                buttonNetworkStream.setBackgroundColor(Color.RED)
            } else {
                if (wakeLock.isHeld && !WLfile) releaseWakelock()
                buttonNetworkStream.setBackgroundColor(
                    Color.GRAY
                )
            }
        }

        checkBoxTimeStamp.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                isWriteTimeStamp = isChecked
            }
        }
        checkBoxLabel.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                isWriteLabel = isChecked
            }
        }

        switchShowGraph.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                // code to switch graph
                if (isChecked) {
                    frameLayOut.visibility = View.VISIBLE
                } else {
                    frameLayOut.visibility = View.GONE
                }
            }
        }
        switchUseService.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                if (isChecked) {
                    Intent(owner, ExampleService::class.java).also { intent ->
                        owner.startService(intent)
                    }
                } else {
                    if (owner.mBound) {
                        owner.unbindService(owner.mConnection)
                        owner.mBound = false
                    }

                    Intent(owner, ExampleService::class.java).also { intent ->
                        owner.stopService(intent)
                    }
                }
            }
        }

        buttonBindService.setOnClickListener {
            owner.bindService(
                Intent(owner, ExampleService::class.java), owner.mConnection,
                Context.BIND_AUTO_CREATE
            )
        }

        buttonSayHello.setOnClickListener {
            owner.sayHello()
        }

        buttonDimDisplay.setOnClickListener {
            val wakelock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AccData::DisplayDimWakeLock").apply {
                    acquire()
                }
            }
        }

        buttonPickFile.setOnClickListener {

        }

        buttonUploadFile.setOnClickListener {

        }


        val readSensor = ReadSensor(this.applicationContext, this)
        readSensor.run(true, 0)
        try {
            spinnerUpdateRate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    itemSelected: View?, selectedItemPosition: Int, selectedId: Long
                ) {
                    try {
                        readSensor.changeUpdateRate(resources.getStringArray(R.array.updateRates)[selectedItemPosition].toString())
                    } catch (e: kotlin.UninitializedPropertyAccessException) {
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        } catch (e: Throwable) {
        }

        registerReceiver(
            mMessageReceiver, IntentFilter("AccelerometerDataUpdates")
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        PermissionRequester(this).result(requestCode, permissions, grantResults).onGranted {
            Toast.makeText(
                this,
                "Permission granted to read your External storage",
                Toast.LENGTH_SHORT
            ).show()
        }.onDenied {
            Toast.makeText(
                this,
                "Permission denied to read your External storage",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("Status")
            val b = intent.getBundleExtra("Acceleration")
            val accData = b.getParcelable<AccData>("SensorEvent")
            if (accData != null) {
                this@MainActivity.TextViewAccelerometer.text =
                    "accelearation x: ${accData.x} y: ${accData.y} z: ${accData.z} "
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val settings = PreferenceManager.getDefaultSharedPreferences(this)
        //val language = settings.getString("language", "")
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    override fun onPause() {
        super.onPause()

    }

    fun updateAccelerationLabel(event: SensorEvent?) {
        if (event != null) {
            label_acceleration.text =
                resources.getString(
                    R.string.label_acceleration,
                    event.values[0],
                    event.values[1],
                    event.values[2]
                )
        }
    }

    fun acquireWakelock() {
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AccData::MyWakelockTag").apply {
                acquire()
            }
        }
    }

    fun releaseWakelock() {
        if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
    }

    fun getUnixTimestamp(): Long {
        //val unixTime = System.currentTimeMillis() / 1000L
        val unixTime = System.currentTimeMillis()
        return unixTime
    }

    fun updateRateLabel() {
        val currentTimestamp = getUnixTimestamp()
        val time_delta = currentTimestamp - lastUnixTimestamp
        lastUnixTimestamp = currentTimestamp
        try {
            val update_rate = 1000 / time_delta
            if (lastUpdateRate != update_rate) {
                textViewUpdRateLabel.text = update_rate.toString()
            }
            lastUpdateRate = update_rate
        } catch (e: java.lang.ArithmeticException) {
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                this.startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    var mService: Messenger? = null
    var mBound: Boolean = false

    val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            mBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            mBound = false
        }
    }

    fun sayHello() {
        if (!mBound) return
        val msg = Message.obtain(null, ExampleService.MSG_SAY_HELLO, 0, 0)
        try {
            mService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

}
