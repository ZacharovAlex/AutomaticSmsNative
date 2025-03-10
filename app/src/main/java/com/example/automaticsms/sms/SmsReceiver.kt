package com.example.automaticsms.sms
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
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

//interface PostSmsCallback {
//    fun onSuccess(response: String)
//    fun onFailure(error: String)
//}

fun postSmsData(url: String, data: String, db: SmsDb, timeStamp: Long) {
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
            db.getDao().updateSmsFlag(timeStamp, -1) // -1 - отправлено, 0 - пока не отправлено
            // если больше 0 - счетчик неудачных попыток, но это потом
        }
    })
}

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val db = SmsDb.getDb(context)
            var address: String
            var body: String
            var timeStamp: Long

            for (message in messages) {
                body = message.messageBody.toString()
                address = message.originatingAddress.toString()
                timeStamp = message.timestampMillis

                val sms = SmsItem(null, timeStamp, address, body, 0)
                CoroutineScope(Dispatchers.IO).launch {
                    db.getDao().insertSms(sms)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    postSmsData(urlString, body, db, timeStamp)
                    // Попытка отослать на сервер сразу при приеме
                }
            }
        }
    }
}
