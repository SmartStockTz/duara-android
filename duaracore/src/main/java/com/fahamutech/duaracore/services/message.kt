package com.fahamutech.duaracore.services

import com.fahamutech.duaracore.models.MessageRemote
import com.fahamutech.duaracore.models.MessageRemoteResponse
import com.fahamutech.duaracore.utils.baseUrlIpfs
import com.fahamutech.duaracore.utils.getHttpClient
import retrofit2.Call
import retrofit2.await
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private interface MessageFunctions {
    @POST("/message/send")
    fun send(@Body data: List<MessageRemote>): Call<List<MessageRemoteResponse>>

    @GET("/ipfs/{cid}")
    fun retrieve(@Path("cid") cid: String): Call<MessageRemote>
}

suspend fun sendMessage(messages: List<MessageRemote>): List<MessageRemoteResponse> {
    return getHttpClient(MessageFunctions::class.java).send(messages).await()
}

suspend fun retrieveMessage(cid: String): MessageRemote {
    return getHttpClient(MessageFunctions::class.java, baseUrlIpfs).retrieve(cid).await()
}



