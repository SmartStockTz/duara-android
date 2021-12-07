package com.fahamutech.duara.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fahamutech.duara.models.IdentityModel
import com.fahamutech.duara.models.UserModel
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val baseUrl = "https://maduara-faas.bfast.fahamutech.com"

private interface AccountService {
    @GET("/account/identity")
    fun identity(): Call<IdentityModel>
}

private fun getHttpClient(): AccountService {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()
    return retrofit.create(AccountService::class.java)
}

private fun errorMessage(message: String, context: Context) {
    Toast.makeText(
        context, message,
        Toast.LENGTH_LONG
    ).show()
}

suspend fun getIdentity(nickname: String, context: Context, onFinish: (user: UserModel?) -> Unit) {
    try {
        val identity = getHttpClient().identity().await()
        Log.e("IDENTITY", identity.did)
        if (identity.did.isEmpty()) {
            errorMessage("Imeshindwa kitengeza utambulisho wako, jaribu tena", context)
            onFinish(null)
        } else {
            getFcmToken(context) { token ->
                val user = UserModel()
                user.did = identity.did
                user.nickname = nickname
                user.picture = ""
                user.priv = identity.priv
                user.pub = identity.pub
                user.token = token
                saveUser(user)
                onFinish(user)
            }
        }
    } catch (e: Throwable) {
        errorMessage(e.message!!, context)
        onFinish(null)
    }
}

fun getFcmToken(context: Context, onFinish: (token: String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener {
        if (!it.isSuccessful) {
            errorMessage("Imeshindwa fungua akaunti, jaribu tena", context)
            return@addOnCompleteListener
        }
        val token = it.result!!
        Log.e("FCM TOKEN", token)
        onFinish(token)
    }
}