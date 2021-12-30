package com.fahamutech.duaracore.services

import com.fahamutech.duaracore.models.MessageRemote
import com.fahamutech.duaracore.models.MessageRemoteResponse
import com.fahamutech.duaracore.models.UploadFileResponse
import com.fahamutech.duaracore.utils.baseUrlIpfs
import com.fahamutech.duaracore.utils.getHttpClient
import okhttp3.MultipartBody
import okhttp3.Response
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

suspend fun sendMessage(messages: List<MessageRemote>): List<MessageRemoteResponse> {
    return getHttpClient(MessageFunctions::class.java).send(messages).await()
}

suspend fun retrieveMessage(cid: String): MessageRemote {
    return getHttpClient(MessageFunctions::class.java, baseUrlIpfs).retrieve(cid).await()
}



