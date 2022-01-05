package com.fahamutech.duaracore.services

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.fahamutech.duaracore.models.*
import com.fahamutech.duaracore.utils.encryptMessage
import com.fahamutech.duaracore.utils.getHttpClient
import com.fahamutech.duaracore.utils.getHttpIpfsClient
import com.fahamutech.duaracore.utils.stringFromDate
import com.fahamutech.duaracore.workers.startSendMessageWorker
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.await
import retrofit2.http.*
import java.util.*

interface MessageFunctions {
    @POST("/message/send")
    fun send(@Body data: List<MessageRemote>): Call<List<MessageRemoteResponse>>

    @GET("/ipfs/{cid}")
    fun retrieve(@Path("cid") cid: String): Call<MessageRemote>

    @Multipart
    @POST("/storage/{applicationId}")
    fun uploadImage(
        @Part body: MultipartBody.Part,
        @Path("applicationId") appId: String
    ): Call<UploadFileResponse>

    @GET
    fun downloadImage(@Url url: String): Call<ResponseBody>
}

suspend fun sendMessage(
    messages: List<MessageRemote>,
    context: Context
): List<MessageRemoteResponse> {
    return getHttpClient(MessageFunctions::class.java, context).send(messages).await()
}

suspend fun retrieveMessage(cid: String, context: Context): MessageRemote {
    return getHttpIpfsClient(MessageFunctions::class.java, context).retrieve(cid).await()
}

suspend fun saveMessageLocalForSend(
    maongezi: Maongezi, message: String, userModel: UserModel, context: Context
) {
    val storage = DuaraStorage.getInstance(context)
    val date = stringFromDate(Date())
    val messageLocal = Message(
        date = date,
        content = message,
        duara_id = maongezi.receiver_duara_id,
        receiver_pubkey = maongezi.receiver_pubkey,
        sender_pubkey = userModel.pub,
        sender_nickname = userModel.nickname,
        receiver_nickname = maongezi.receiver_nickname,
        maongezi_id = maongezi.id,
        type = MessageType.TEXT.toString()
    )
    val messageOutbox = encryptMessage(messageLocal, context)
    storage.withTransaction {
        storage.message().save(messageLocal)
        storage.messageOutbox().save(messageOutbox)
        storage.maongezi().updateOngeziLastSeen(maongezi.id, date)
    }
    startSendMessageWorker(context)
}

