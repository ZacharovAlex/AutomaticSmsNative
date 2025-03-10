package com.example.automaticsms.sms

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SmsItem (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "timeStamp")
    val timeStamp: Long,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "messageBody")
    val messageBody: String,
    @ColumnInfo(name = "flag")
    var flag: Int,
)