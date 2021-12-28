package com.fahamutech.duaracore.services

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.fahamutech.duaracore.models.UpdateNicknameRequest
import com.fahamutech.duaracore.models.UpdatePictureRequest
import com.fahamutech.duaracore.models.UploadFileResponse
import com.fahamutech.duaracore.models.UserModel
import com.fahamutech.duaracore.services.DuaraStorage
import com.fahamutech.duaracore.utils.generateKeyPair
import com.fahamutech.duaracore.utils.stringToSHA256
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
    fun updateToken(@Body data: UpdateNicknameRequest): Call<String>
}

suspend fun getIdentity(nickname: String, image: String, context: Context): UserModel {
    return withContext(Dispatchers.IO) {
        val storage = DuaraStorage.getInstance(context)
        val identityModel = generateKeyPair()
        val token = getFcmToken()
        val user = UserModel(
            nickname = nickname,
            picture = image,
            priv = identityModel.priv,
            pub = identityModel.pub,
            token = token
        )
        storage.user().saveUser(user)
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
        if (id == null) {
            id = UUID.randomUUID().toString()
        }
        return@withContext stringToSHA256(id)
    }
}








