package com.fahamutech.duara.workers

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessage : FirebaseMessagingService() {

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

