package com.example.automaticsms

data class SystemDetails(
    val brand: String = "",
    val deviceId: String = "",
    val model: String = "",
    val id: String = "",
    val sdk: Int = 0,
    val manufacturer: String = "",
    val user: String = "",
    val type: String = "",
    val base: Int = 0,
    val incremental: String = "",
    val board: String = "",
    val host: String = "",
    val fingerPrint: String = "",
    val versionCode: String = "",
    val imei: String = "",
    val operatorName: String = "",
    val charging: Boolean = false,
    val usbCharge: Boolean = false,
    val acCharge: Boolean = false,
    val batteryPct: Float = 0.0f,
) : java.io.Serializable
