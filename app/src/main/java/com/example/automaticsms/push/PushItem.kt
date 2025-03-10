package com.example.automaticsms.push

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PushItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "timeStamp")
    val timeStamp: Long,
    @ColumnInfo(name = "messageTitle")
    val messageTitle: String,
    @ColumnInfo(name = "messageText")
    val messageText: String,
    @ColumnInfo(name = "packageName")
    val packageName: String,
    @ColumnInfo(name = "flag")
    var flag: Int,
)
