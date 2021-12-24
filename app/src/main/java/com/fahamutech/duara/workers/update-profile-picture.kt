package com.fahamutech.duara.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.withTransaction
import androidx.work.*
import com.fahamutech.duara.models.UpdatePictureRequest
import com.fahamutech.duara.models.XY
import com.fahamutech.duara.services.AccountFunctions
import com.fahamutech.duara.services.DuaraStorage
import com.fahamutech.duara.utils.baseUrl
import com.fahamutech.duara.utils.getHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.await
import java.io.File
import java.util.concurrent.TimeUnit
import java.io.FileInputStream


class UploadProfilePictureWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            if (runAttemptCount > 20) {
                return@withContext Result.failure()
            }
            val path = inputData.getString("path")
            val type = inputData.getString("type")
            val url = uploadImageToServer(path, type, applicationContext)
            Result.success(workDataOf(Pair("url", url)))
        } catch (e: Exception) {
            Log.e("UPLOAD PICTURE ERROR", e.toString())
            Result.retry()
        }
    }
}


class UpdateProfilePictureUrlWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val storage = DuaraStorage.getInstance(applicationContext)
        val url = inputData.getString("url")
        return@withContext try {
            if (runAttemptCount > 20) return@withContext Result.failure()
            if (url == null) return@withContext Result.success()
            val user = storage.user().getUser() ?: return@withContext Result.success()
            val updatePicture = UpdatePictureRequest()
            updatePicture.url = url
            updatePicture.xy = XY(
                x = user.pub?.x,
                y = user.pub?.y
            )
            val call =
                getHttpClient(AccountFunctions::class.java).updatePicture(updatePicture).await()
            user.picture = call
            storage.withTransaction {
                storage.user().saveUser(user)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("UPDATE PICTURE ERROR", e.toString())
            Result.retry()
        }
    }
}


fun startUploadAndUpdateProfilePicture(path: String, type: String?, context: Context) {
    WorkManager.getInstance(context)
        .beginWith(oneTimeUploadWorker(path, type))
        .then(oneTimeUpdateWorker())
        .enqueue()
}


private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

private fun oneTimeUploadWorker(path: String, type: String?): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<UploadProfilePictureWorker>()
        .setConstraints(constraints)
        .setInputData(workDataOf(Pair("path", path), Pair("type", type)))
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
}

private fun oneTimeUpdateWorker(): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<UpdateProfilePictureUrlWorker>()
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
}

suspend fun uploadImageToServer(path: String?, type: String?, context: Context): String? {
    return if (path != null && type != null) {
        val uri = Uri.parse(path)
        val bytes: ByteArray? = if (path.startsWith("content:")) {
            val istrm = context.contentResolver?.openInputStream(uri)
            istrm?.use {
                it.readBytes()
            }
        } else {
            val a = FileInputStream(File(path))
            a.use {
                it.readBytes()
            }
        }
        if (bytes == null) {
            return null
        }
        val name = "picture." + type.split("/")[1]
        val filePart = MultipartBody.Part.createFormData(
            name, name,
            RequestBody.create(MediaType.parse(type), bytes)
        )
        val call =
            getHttpClient(AccountFunctions::class.java).uploadPicture(filePart).await()
        return baseUrl + call.urls[0]
    }else null
}







