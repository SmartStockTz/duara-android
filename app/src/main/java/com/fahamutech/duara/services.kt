package com.fahamutech.duara

import android.util.Log
import com.fahamutech.duaracore.utils.OPTIONS
import com.fahamutech.duaracore.workers.startReceiveAndRetrieveMessageWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    init {
        OPTIONS.ChildMainActivity = DuaraAppActivity::class.java
    }

    override fun onNewToken(token: String) {
        Log.e("TOKEN REFRESH", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("RECEIVED", remoteMessage.data.toString())
        remoteMessage.data["cid"]?.let {
            startReceiveAndRetrieveMessageWorker(it, applicationContext)
        }
    }
}
