package com.fahamutech.duaracore.workers
//
//import android.util.Log
//import com.fahamutech.duaracore.utils.OPTIONS
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//
//abstract class DuaraFCMService : FirebaseMessagingService() {
//    init {
//        OPTIONS.ChildMainActivity = this.getMainActivityClass()
//    }
//
//    abstract fun getMainActivityClass(): Class<*>
//
//    override fun onNewToken(token: String) {
//        Log.e("TOKEN REFRESH", "Refreshed token: $token")
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.e("RECEIVED", remoteMessage.data.toString())
//        remoteMessage.data["cid"]?.let {
//            startReceiveAndRetrieveMessageWorker(it, applicationContext)
//        }
//    }
//}
//
