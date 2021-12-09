package com.fahamutech.duara.utils

import android.content.Context
import android.widget.Toast
import okhttp3.OkHttpClient
import okio.Timeout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

const val baseUrl = "https://maduara-faas.bfast.fahamutech.com"

fun <T> getHttpClient(clazz: Class<T>): T {
    val okHttClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5,TimeUnit.MINUTES)
        .build()
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttClient)
        .baseUrl(baseUrl)
        .build()
    return retrofit.create(clazz)
}

fun stringToSHA256(data: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashInByte = digest.digest(data.toByteArray())
    val sb = StringBuilder()
    for (b in hashInByte) {
        sb.append(String.format("%02x", b))
    }
    return sb.toString()
}

fun message(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

suspend fun withTryCatch(run: suspend () -> Unit, onError: (message: String) -> Unit) {
    try {
        run()
    } catch (e: Throwable) {
        onError(e.message ?: e.toString())
    }
}