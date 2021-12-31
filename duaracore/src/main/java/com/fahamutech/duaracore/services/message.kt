package com.fahamutech.duaracore.services

import android.content.Context
import com.fahamutech.duaracore.models.MessageRemote
import com.fahamutech.duaracore.models.MessageRemoteResponse
import com.fahamutech.duaracore.models.UploadFileResponse
import com.fahamutech.duaracore.utils.getHttpClient
import com.fahamutech.duaracore.utils.getHttpIpfsClient
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.await
import retrofit2.http.*

interface MessageFunctions {
    @POST("/message/send")
    fun send(@Body data: List<MessageRemote>): Call<List<MessageRemoteResponse>>
    @GET("/ipfs/{cid}")
    fun retrieve(@Path("cid") cid: String): Call<MessageRemote>
    @Multipart
    @POST("/storage/maduara")
    fun uploadImage(@Part body: MultipartBody.Part): Call<UploadFileResponse>
    @GET
    fun downloadImage(@Url url: String): Call<ResponseBody>
}

suspend fun sendMessage(messages: List<MessageRemote>, context: Context): List<MessageRemoteResponse> {
    return getHttpClient(MessageFunctions::class.java, context).send(messages).await()
}

suspend fun retrieveMessage(cid: String, context: Context): MessageRemote {
    return getHttpIpfsClient(MessageFunctions::class.java, context).retrieve(cid).await()
}



