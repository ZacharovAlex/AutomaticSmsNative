package com.example.automaticsms.push

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import com.example.automaticsms.db.SmsDb
import com.example.automaticsms.urlString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class NotificationListener : NotificationListenerService() {

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(newNotification: StatusBarNotification) {
        val db = SmsDb.getDb(this)
        val extras = newNotification.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getString("android.text") ?: ""
        val packageName = newNotification.packageName
        //   val channel = extras.getString("android.channel_id")
        val date = newNotification.postTime
        val push = PushItem(null, date, title, text, packageName, 0)
        CoroutineScope(Dispatchers.IO).launch {
            db.getDao().insertPush(push)
        }
        CoroutineScope(Dispatchers.IO).launch {
            postPushData(urlString, text, db, date)
            // Попытка отослать на сервер сразу при приеме
        }
//     Toast.makeText(this, "Notification received ${extras.getString("android.text")}", Toast.LENGTH_LONG).show()
//     Log.d("Response111", "Text: $text")
        super.onNotificationPosted(newNotification)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
//      Toast.makeText(this, "Notification swipped", Toast.LENGTH_LONG).show()
//      System.exit(0);
    }
}

fun postPushData(url: String, data: String, db: SmsDb, timeStamp: Long) {
    val okHttpClient = OkHttpClient()
    val requestBody = data.toRequestBody()
    val request = Request.Builder()
        .post(requestBody)
        .url(url)
        .build()
    okHttpClient.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("Response111", "error: $e")
        }

        override fun onResponse(call: Call, response: Response) {
            db.getDao().updatePushFlag(timeStamp, -1) // -1 - отправлено, 0 - пока не отправлено
            // если больше 0 - счетчик неудачных попыток, но это потом
        }
    })
}
