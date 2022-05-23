package com.fahamutech.duaracore.services

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.fahamutech.duaracore.models.*
import com.fahamutech.duaracore.utils.generateKeyPair
import com.fahamutech.duaracore.utils.getHttpClient
import com.fahamutech.duaracore.utils.stringToSHA256
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.await
import retrofit2.awaitResponse
import retrofit2.http.*
import java.util.*

interface AccountFunctions {
    @Multipart
    @POST("/account/picture/upload")
    fun uploadPicture(@Part body: MultipartBody.Part): Call<UploadFileResponse>

    @POST("/account/picture")
    fun updatePicture(@Body data: UpdatePictureRequest): Call<String>

    @POST("/account/nickname")
    fun updateNickname(@Body data: UpdateNicknameRequest): Call<String>

    @POST("/account/token")
    fun updateToken(@Body data: UpdateTokenRequest): Call<String>

    @GET("/services/{serviceId}/payment/subscription")
    fun subscription(
        @Path("serviceId") serviceId: Int?,
        @Query("x") x: String?,
        @Query("y") y: String?,
        @Query("amount") amount: Int?,
        @Query("mtoa_huduma_x") mtoa_huduma_x: String?
    ): Call<Subscription>

    @POST("/account/login")
    fun mtoaHudumaLogin(@Body data: MtoaHudumaLoginRequest): Call<UserModel>
}

suspend fun getIdentity(
    nickname: String,
    age: String,
    gender: String,
    image: String,
    context: Context
): UserModel {
//    return withContext(Dispatchers.IO) {
    val storage = DuaraStorage.getInstance(context)
    val identityModel = generateKeyPair()
    val token = getFcmToken()
    val user = UserModel(
        nickname = nickname,
        age = age,
        gender = gender,
        picture = image,
        priv = identityModel.priv,
        pub = identityModel.pub,
        token = token
    )
    storage.user().saveUser(user)
    return user
//        return@withContext user
//    }
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
        if (id == null) {
            id = UUID.randomUUID().toString()
        }
        return@withContext stringToSHA256(id)
    }
}

suspend fun getSubscription(request: SubscriptionRequest, context: Context): Subscription {
    return getHttpClient(AccountFunctions::class.java, context).subscription(
        x = request.x, y = request.y, serviceId = request.service, amount = request.amount,
        mtoa_huduma_x = request.mtoa_huduma_x
    ).await()
}

suspend fun mtoaHudumaLogin(username: String, password: String, context: Context): UserModel {
    val data = MtoaHudumaLoginRequest(username, password)
    val userResponse =
        getHttpClient(AccountFunctions::class.java, context).mtoaHudumaLogin(data).awaitResponse()
    return if (userResponse.isSuccessful) {
        val user = userResponse.body() ?: throw Throwable("hakuna huyo mtoa huduma")
        val storage = DuaraStorage.getInstance(context)
        user.type = "mtoa_huduma"
        storage.user().saveUser(user)
        user
    } else {
        val error = Gson().fromJson(userResponse.errorBody()?.string(), MutableMap::class.java)
        throw Throwable(error["message"].toString())
    }
}





