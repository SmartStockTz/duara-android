package com.fahamutech.duaracore.workers

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import androidx.work.*
import com.fahamutech.duaracore.models.XY
import com.fahamutech.duaracore.services.AccountFunctions
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.utils.getHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import java.util.concurrent.TimeUnit
import com.fahamutech.duaracore.models.UpdateTokenRequest


class UpdateProfileTokenWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val storage = DuaraStorage.getInstance(applicationContext)
        val token = inputData.getString("token")
        return@withContext try {
            if (runAttemptCount > 100) return@withContext Result.failure()
            if (token == null) return@withContext Result.success()
            val user = storage.user().getUser() ?: return@withContext Result.retry()
            val updateToken = UpdateTokenRequest()
            updateToken.token = token
            updateToken.xy = XY(
                x = user.pub?.x,
                y = user.pub?.y
            )
            val call =
                getHttpClient(AccountFunctions::class.java, applicationContext).updateToken(
                    updateToken
                ).await()
            user.token = call
            storage.withTransaction {
                storage.user().saveUser(user)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("UPDATE TOKEN ERROR", e.toString())
            Result.retry()
        }
    }
}


fun startUploadToken(token: String, context: Context) {
    WorkManager.getInstance(context)
        .enqueue(oneTimeTokenUpdateWorker(token))
}


private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

private fun oneTimeTokenUpdateWorker(token: String): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<UpdateProfileTokenWorker>()
        .setConstraints(constraints)
        .setInputData(workDataOf(Pair("token", token)))
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
}






