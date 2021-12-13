package com.fahamutech.duara.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.fahamutech.duara.models.MessageLocalOutBox
import com.fahamutech.duara.models.MessageRemote
import com.fahamutech.duara.services.deleteMessageInWaitToBeSent
import com.fahamutech.duara.services.getMessagesWaitToBeSent
import com.fahamutech.duara.services.initLocalDatabase
import com.fahamutech.duara.services.sendMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SendMessagesWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        initLocalDatabase(applicationContext)
        return@withContext try {
            if (runAttemptCount > 20) {
                return@withContext Result.failure()
            }
            val messages: List<MessageLocalOutBox> = getMessagesWaitToBeSent()
            Log.e("START SENDING...", messages.size.toString())
            if (messages.toMutableList().isEmpty()) {
                Log.e("MESSAGE SENT", "0")
                return@withContext Result.success()
            }
            val messagesRemote = messages.map {
                MessageRemote(
                    message = it.message,
                    from = it.from!!,
                    to = it.to
                )
            }
            val response = sendMessage(messagesRemote)
            Log.e("MESSAGE SENT", response.map { it.cid }.toString())
            messages.forEach {
                deleteMessageInWaitToBeSent(it.id)
            }
            val outputData = workDataOf(
                "message_cids" to response.map { it.cid }.joinToString(",")
            )
            Result.success(outputData)
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








