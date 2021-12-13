package com.fahamutech.duara.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color.GREEN
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.work.*
import com.fahamutech.duara.DuaraApp
import com.fahamutech.duara.R
import com.fahamutech.duara.models.*
import com.fahamutech.duara.services.*
import com.fahamutech.duara.utils.decryptMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class ReceiveMessagesWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        initLocalDatabase(applicationContext)
        return@withContext try {
            if (runAttemptCount > 20) {
                return@withContext Result.failure()
            }
            val cid = inputData.getString("cid")
            val messageLocalSignature = MessageLocalSignature()
            messageLocalSignature.cid = cid!!
            getRealm().executeTransaction {
                it.insertOrUpdate(messageLocalSignature)
            }
//            Log.e("SAVE SMS CID", cid)
            val outputData = workDataOf("cid" to cid)
            Result.success(outputData)
        } catch (e: Exception) {
            Log.e("SAVE CID ERROR", e.toString())
            Result.retry()
        }
    }
}

class RetrieveMessagesWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        initLocalDatabase(applicationContext)
        return@withContext try {
            if (runAttemptCount > 20) {
                return@withContext Result.failure()
            }
            var messagesSign = mutableListOf<MessageLocalSignature>()
            val a = getRealm().where(MessageLocalSignature::class.java).findAll()
            if (a != null) {
                messagesSign = getRealm().copyFromRealm(a)
            }
            if (messagesSign.isNotEmpty()) {
                messagesSign.forEach {
                    try {
                        val mr = retrieveMessage(it.cid ?: "")
                        val m = decryptMessage(mr)
                        m.status = MessageStatus.UNREAD.toString()
                        getRealm().executeTransaction { realm ->
                            val ongezi = realm.where(Ongezi::class.java)
                                .equalTo("id", m.duara_pub?.x)
                                .findFirst()
                            if (ongezi == null) {
                                val d = Ongezi()
                                d.duara_nickname = m.fromNickname
                                d.duara_pub = m.duara_pub
                                d.duara_id = m.duara_id
                                d.id = m.duara_pub!!.x
                                realm.insertOrUpdate(d)
                            }
                            m.duara_id = m.duara_pub!!.x
                            realm.insertOrUpdate(m)
                            realm.where(MessageLocalSignature::class.java)
                                .equalTo("cid", it.cid)
                                .findAll()?.deleteAllFromRealm()
                        }
                        sendNotification(applicationContext, m)
                    } catch (e: java.lang.Exception) {
                        Log.e("****", e.toString())
                    }
                }
                Result.success()
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("RETRIEVE CID ERROR", e.toString())
            Result.retry()
        }
    }
}

private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

private fun oneTimeReceiveMessageWorker(cid: String): OneTimeWorkRequest {
    val input = workDataOf("cid" to cid)
    return OneTimeWorkRequestBuilder<ReceiveMessagesWorker>()
        .setInputData(input)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
}

private fun oneTimeRetrieveMessageWorker(): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<RetrieveMessagesWorker>()
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
}

fun periodicTimeRetrieveMessageWorker(): PeriodicWorkRequest {
    return PeriodicWorkRequestBuilder<RetrieveMessagesWorker>(
        PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
        TimeUnit.MILLISECONDS
    ).setConstraints(constraints).setBackoffCriteria(
        BackoffPolicy.EXPONENTIAL,
        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
        TimeUnit.MILLISECONDS
    ).build()

}

fun startReceiveAndRetrieveMessageWorker(cid: String, context: Context) {
    WorkManager.getInstance(context)
        .beginWith(oneTimeReceiveMessageWorker(cid))
        .then(oneTimeRetrieveMessageWorker())
        .enqueue()
}

fun startPeriodicalRetrieveMessageWorker(context: Context) {
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "retrieve_messages_from_ipfs",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicTimeRetrieveMessageWorker()
        )
}


private fun sendNotification(context: Context, messageLocal: MessageLocal) {
    val id = Math.random().roundToInt()
    val intent = Intent(context, DuaraApp::class.java)
    intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
    intent.putExtra(messageLocal.duara_id, messageLocal.id)
    val NOTIFICATION_CHANNEL = messageLocal.duara_id!!
    val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val titleNotification = messageLocal.fromNickname
    val subtitleNotification = messageLocal.content
    val pendingIntent = if (SDK_INT >= Build.VERSION_CODES.M) {
        getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        getActivity(context, id, intent, 0)
    }
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(titleNotification)
        .setContentText(subtitleNotification)
        .setDefaults(DEFAULT_ALL)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notification.priority = PRIORITY_MAX

    if (SDK_INT >= O) {
        notification.setChannelId(NOTIFICATION_CHANNEL)
        val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
            .setContentType(CONTENT_TYPE_SONIFICATION).build()
        val NOTIFICATION_NAME = messageLocal.fromNickname
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.enableLights(true)
        channel.lightColor = GREEN
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setSound(ringtoneManager, audioAttributes)
        notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(id, notification.build())
}




