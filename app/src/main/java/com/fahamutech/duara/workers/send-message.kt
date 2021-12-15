package com.fahamutech.duara.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.fahamutech.duara.models.MessageOutBox
import com.fahamutech.duara.models.MessageRemote
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.services.sendMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SendMessagesWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val storage = DuaraStorage.getInstance(applicationContext)
        return@withContext try {
            if (runAttemptCount > 20) {
                return@withContext Result.failure()
            }
            val messages: List<MessageOutBox> = storage.messageOutbox().all()
            Log.e("START SENDING...", messages.size.toString())
            if (messages.toMutableList().isEmpty()) {
                Log.e("MESSAGE SENT", "0")
                return@withContext Result.success()
            }
            val messagesRemote = messages.map {
                MessageRemote(
                    message = it.message,
                    sender_pubkey = it.sender_pubkey!!,
                    receiver_pubkey = it.receiver_pubkey!!
                )
            }
            val response = sendMessage(messagesRemote)
            val cids = response.map { it.cid }
            Log.e("MESSAGE SENT", cids.toString())
            messages.forEach {
                storage.messageOutbox().deleteById(it.id)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("MESSAGE SENT ERROR", e.toString())
            Result.retry()
        }
    }

}

private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

private fun oneTimeSendMessageWorker(): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<SendMessagesWorker>()
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
}

fun periodicTimeSendMessageWorker(): PeriodicWorkRequest {
    return PeriodicWorkRequestBuilder<SendMessagesWorker>(
        PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
        TimeUnit.MILLISECONDS
    ).setConstraints(constraints).setBackoffCriteria(
        BackoffPolicy.EXPONENTIAL,
        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
        TimeUnit.MILLISECONDS
    ).build()

}

fun startSendMessageWorker(context: Context) {
    WorkManager.getInstance(context)
        .enqueue(oneTimeSendMessageWorker())
}

fun startPeriodicalSendMessageWorker(context: Context) {
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "send_messages_from_local",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicTimeSendMessageWorker()
        )
}








