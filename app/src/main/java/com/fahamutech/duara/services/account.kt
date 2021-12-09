package com.fahamutech.duara.services

import android.util.Log
import com.fahamutech.duara.models.IdentityModel
import com.fahamutech.duara.models.UserModel
import com.fahamutech.duara.utils.getHttpClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.await
import retrofit2.http.GET

private interface AccountService {
    @GET("/account/identity")
    fun identity(): Call<IdentityModel>
}

suspend fun getIdentity(nickname: String): UserModel {
    return withContext(Dispatchers.IO) {
        val identity = getHttpClient(AccountService::class.java).identity().await()
        Log.e("IDENTITY", identity.did)
        if (identity.did.isEmpty()) {
            throw Throwable(message = "Imeshindwa kitengeza utambulisho wako, jaribu tena")
        } else {
            val token = getFcmToken()
            val user = UserModel()
            user.did = identity.did
            user.nickname = nickname
            user.picture = ""
            user.priv = identity.priv
            user.pub = identity.pub
            user.token = token
            saveUser(user)
            return@withContext user
        }
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
