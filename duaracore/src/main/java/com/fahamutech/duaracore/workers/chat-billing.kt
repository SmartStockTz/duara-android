package com.fahamutech.duaracore.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.fahamutech.duaracore.R
import com.fahamutech.duaracore.models.Maongezi
import com.fahamutech.duaracore.models.Message
import com.fahamutech.duaracore.models.Subscription
import com.fahamutech.duaracore.models.SubscriptionRequest
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.services.getSubscription
import com.fahamutech.duaracore.services.saveMessageLocalForSend
import com.fahamutech.duaracore.utils.dateFromString
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

private const val SUB_PREF_NAME = "iyiutiti87t87tuysubscription"
private val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

class ChatBillingWorker(
    context: Context, params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (runAttemptCount > 100) {
            return@withContext Result.failure()
        }
        val storage = DuaraStorage.getInstance(applicationContext)
        val user = storage.user().getUser()
        val chatBilling = user?.payment ?: 0
        if (chatBilling <= 0) {
            return@withContext Result.success()
        }
        val maongeziString = inputData.getString("maongezi")
        val messageString = inputData.getString("message")
        if (messageString?.contains("bfast.fahamutech.com/services") == true) {
            return@withContext Result.success()
        }
        val maongezi = Gson().fromJson(maongeziString, Maongezi::class.java)
        if (maongezi == null) Result.success()
        val x = maongezi.receiver_pubkey?.x ?: "na"
        return@withContext try {
            var subscription = getLocalSubscription(applicationContext, x)
            if (user == null) Result.success()
            if (subscription == null) {
                val subsRequest = SubscriptionRequest(
                    maongezi?.receiver_pubkey?.x,
                    maongezi.receiver_pubkey?.y,
                    applicationContext.resources.getInteger(R.integer.chat_service),
                    user?.payment ?: 0,
                    mtoa_huduma_x = user?.pub?.x ?: ""
                )
                subscription = getSubscription(subsRequest, applicationContext)
                updateLocalSubscription(subscription, x, applicationContext)
            }
            if (isNotPaidOrExpire(subscription)) {
                removeLocalSubscription(applicationContext, x)
                saveMessageLocalForSend(
                    maongezi, getBillingMessage(subscription), user!!, applicationContext
                )
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("CHAT BILLING", e.toString())
            Result.retry()
        }
    }

    private fun getBillingMessage(subscription: Subscription): String {
        return subscription.how ?: ""
    }

    private fun getLocalSubscription(context: Context, x: String): Subscription? {
        val preference =
            context.getSharedPreferences(SUB_PREF_NAME, Context.MODE_PRIVATE) ?: return null
        val subscriptionString = preference.getString("subs_$x", null)
        return if (subscriptionString != null) {
            Gson().fromJson(subscriptionString, Subscription::class.java)
        } else null
    }

    private fun updateLocalSubscription(subscription: Subscription, x: String, context: Context) {
        val preference = context.getSharedPreferences(SUB_PREF_NAME, Context.MODE_PRIVATE) ?: return
        with(preference.edit()) {
            putString("subs_$x", Gson().toJson(subscription))
            commit()
        }
    }

    private fun removeLocalSubscription(context: Context, x: String) {
        val preference = context.getSharedPreferences(SUB_PREF_NAME, Context.MODE_PRIVATE) ?: return
        with(preference.edit()) {
            remove("subs_$x")
            commit()
        }
    }
}

fun isNotPaidOrExpire(subscription: Subscription): Boolean {
    if (subscription.paid == false) return true
    return if (subscription.expire?.isEmpty() != true) {
        val expireDate = dateFromString(subscription.expire!!)
        val now = Date()
        now.after(expireDate)
    } else subscription.paid ?: true
}

fun oneTimeSendBillingMessageWorker(): OneTimeWorkRequest {
    return OneTimeWorkRequestBuilder<ChatBillingWorker>()
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        ).build()
}







