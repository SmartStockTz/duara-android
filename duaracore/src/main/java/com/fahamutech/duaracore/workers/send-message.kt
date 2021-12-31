package com.fahamutech.duaracore.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.fahamutech.duaracore.models.Message
import com.fahamutech.duaracore.models.MessageOutBox
import com.fahamutech.duaracore.models.MessageRemote
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.services.MessageFunctions
import com.fahamutech.duaracore.services.sendMessage
import com.fahamutech.duaracore.utils.baseUrl
import com.fahamutech.duaracore.utils.encryptImageMessage
import com.fahamutech.duaracore.utils.encryptMessage
import com.fahamutech.duaracore.utils.getHttpClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.await
import java.io.File
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

class SendImageMessagesWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val storage = DuaraStorage.getInstance(applicationContext)
        return@withContext try {
            if (runAttemptCount > 100) {
                return@withContext Result.failure()
            }
            val imageMessageString = inputData.getString("image_message")
            if (imageMessageString != null) {
                val message = Gson().fromJson(imageMessageString, Message::class.java)
                val path = message.content
                val file = File(path)
                if (file.exists()) {
                    val fileByte = file.readBytes()
                    val base64data =
                        encryptImageMessage(message.receiver_pubkey!!, fileByte, applicationContext)
                    val filePart = MultipartBody.Part.createFormData(
                        "image", "image.txt",
                        RequestBody.create(MediaType.parse("plain/text"), base64data.toByteArray())
                    )
                    val response =
                        getHttpClient(MessageFunctions::class.java).uploadImage(filePart).await()
                    val url = baseUrl + response.urls[0]
                    message.content = url
                    val outbox = encryptMessage(message, applicationContext)
                    storage.messageOutbox().save(outbox)
                    startSendMessageWorker(applicationContext)
//                    Log.e("TAG***UPLOAD", url)
                }
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

private fun oneTimeSendImageMessageWorker(message: Message): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<SendImageMessagesWorker>()
        .setConstraints(constraints)
        .setInputData(
            workDataOf(
                Pair(
                    "image_message",
                    Gson().toJson(message, Message::class.java)
                )
            )
        )
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

fun startSendImageMessageWorker(context: Context, message: Message) {
    WorkManager.getInstance(context)
        .enqueue(oneTimeSendImageMessageWorker(message))
}

fun startPeriodicalSendMessageWorker(context: Context) {
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "send_messages_from_local",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicTimeSendMessageWorker()
        )
}








