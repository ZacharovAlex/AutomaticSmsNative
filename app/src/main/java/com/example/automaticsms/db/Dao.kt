package com.example.automaticsms.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.automaticsms.push.PushItem
import com.example.automaticsms.sms.SmsItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    fun insertSms(smsItem: SmsItem)

    @Query("SELECT * FROM smsItem")
    fun getAllSms(): Flow<List<SmsItem>>

    @Query("SELECT * FROM smsItem WHERE flag >= 0")
    fun getUnsentSms(): List<SmsItem>

    @Query("DELETE FROM smsItem")
    fun deleteAllSms()

    @Query("UPDATE smsItem SET flag = :flag WHERE timeStamp=:timeStamp")
    fun updateSmsFlag(timeStamp: Long, flag: Long)

    @Insert
    fun insertPush(pushItem: PushItem)

    @Query("SELECT * FROM pushItem")
    fun getAllPush(): Flow<List<PushItem>>

    @Query("SELECT * FROM pushItem WHERE flag >= 0")
    fun getUnsentPush(): List<PushItem>

    @Query("DELETE FROM pushItem")
    fun deleteAllPush()

    @Query("UPDATE pushItem SET flag = :flag WHERE timeStamp=:timeStamp")
    fun updatePushFlag(timeStamp: Long, flag: Long)
}