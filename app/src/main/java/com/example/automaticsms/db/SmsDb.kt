package com.example.automaticsms.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.automaticsms.push.PushItem
import com.example.automaticsms.sms.SmsItem

@Database(entities = [SmsItem::class, PushItem::class], version = 1)
abstract class SmsDb : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: SmsDb? = null

        fun getDb(context: Context): SmsDb {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmsDb::class.java, "sms.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}