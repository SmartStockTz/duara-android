package com.fahamutech.duaracore.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.room.withTransaction
import androidx.work.*
import com.fahamutech.duaracore.models.*
import com.fahamutech.duaracore.services.*
import com.fahamutech.duaracore.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

class ReceiveMessagesWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val storage = DuaraStorage.getInstance(applicationContext)
        return@withContext try {
            if (runAttemptCount > 20) {
                return@withContext Result.failure()
            }
            val cid = inputData.getString("cid")
            val messageCid = MessageCID(
                cid = cid!!
            )
            storage.messageCid().save(messageCid)
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
        val storage = DuaraStorage.getInstance(applicationContext)
        return@withContext try {
            if (runAttemptCount > 20) {
                Result.failure()
            } else {
                val messagesSign = storage.messageCid().all()
                if (messagesSign.isNotEmpty()) {
                    messagesSign.forEach {
                        handleNewMessage(it, storage, applicationContext)
                    }
                }
                Result.success()
            }
        } catch (e: Exception) {
//            throw e
            Log.e("RETRIEVE CID ERROR", e.toString())
            Result.retry()
        }
    }
}

private suspend fun handleNewMessage(
    message: MessageCID, storage: DuaraDatabase, context: Context
) {
    try {
        val messageFromCID = retrieveMessage(message.cid ?: "")
        val messageDecrypted = decryptMessage(messageFromCID, context)
        if (messageDecrypted === null) {
            return
        }
        if (messageDecrypted.type == MessageType.IMAGE.toString()) {
//            Log.e("FFFFF", "load image")
            val messageBase64 = getHttpClientPlain(MessageFunctions::class.java)
                .downloadImage(messageDecrypted.content).await()
            val m = withContext(Dispatchers.IO){return@withContext messageBase64.string().orEmpty()}
//            Log.e("FFFFF", m.length.toString())
            val messageBytes = decryptImageMessage(messageDecrypted.sender_pubkey!!, m, context)
            val imageName = "/receive/Duara-${stringToSHA256(messageDecrypted.content)}.jpg"
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                imageName
            )
            if (file.parentFile?.mkdirs() == false) {
                Log.e("LOG_TAG", "Directory not created")
            }
            if (messageBytes != null) {
                Log.e("LOG MB", messageBytes.size.toString())
            }
            FileOutputStream(file).use { fos ->
                fos.write(messageBytes)
            }
            messageDecrypted.content = file.absolutePath
        }
        val mid = messageDecrypted.sender_pubkey!!.x
        messageDecrypted.status = MessageStatus.UNREAD.toString()
        messageDecrypted.date = stringFromDate(Date())
        messageDecrypted.maongezi_id = mid

        val ongezi = storage.maongezi().getOngeziInStore(mid)
        if (ongezi == null) {
            val maongezi = Maongezi(
                id = mid,
                receiver_duara_id = messageDecrypted.duara_id,
                receiver_nickname = messageDecrypted.sender_nickname,
                receiver_pubkey = messageDecrypted.sender_pubkey
            )
            storage.withTransaction {
                storage.maongezi().saveOngezi(maongezi)
                storage.message().save(messageDecrypted)
                storage.messageCid().delete(message.cid)
            }
        } else {
            storage.withTransaction {
                storage.message().save(messageDecrypted)
                storage.maongezi().updateOngeziLastSeen(mid)
                storage.messageCid().delete(message.cid ?: "")
            }
        }
        if (OPTIONS.IS_VISIBLE) {
            playMessageSound(context)
        } else showMessageNotification(context, messageDecrypted)
    } catch (e: Throwable) {
        e.printStackTrace()
        Log.e("Handle message", e.toString())
        throw e
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




