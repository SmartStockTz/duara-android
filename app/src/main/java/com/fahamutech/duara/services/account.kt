package com.fahamutech.duara.services

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings
import android.util.Log
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.utils.generateKeyPair
import com.fahamutech.duara.utils.stringToSHA256
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

suspend fun getIdentity(nickname: String): UserModel {
    return withContext(Dispatchers.IO) {
        val identityModel = generateKeyPair()
        val token = getFcmToken()
        val user = UserModel()
        user.nickname = nickname
        user.picture = ""
        user.priv = identityModel.priv
        user.pub = identityModel.pub
        user.token = token
        saveUser(user)
        return@withContext user
    }
}

suspend fun getFcmToken(): String {
    return withContext(Dispatchers.IO) {
        val it = FirebaseMessaging.getInstance().token.await()
        if (it == null) {
            throw Throwable(message = "Imeshindwa fungua akaunti, jaribu tena")
        } else {
            Log.e("FCM TOKEN", it)
            return@withContext it
        }
    }
}

@SuppressLint("HardwareIds")
suspend fun getDeviceId(contentResolver: ContentResolver): String {
    return withContext(Dispatchers.IO) {
        var id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//        Log.e("USE android D.Id", id)
        if (id == null) {
            id = UUID.randomUUID().toString()
//            Log.e("USE UUID D.Id", id)
        }
        return@withContext stringToSHA256(id)
    }
}
