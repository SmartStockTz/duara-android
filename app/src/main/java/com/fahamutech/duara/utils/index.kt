package com.fahamutech.duara.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.fahamutech.duara.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val baseUrl = "https://maduara-faas.bfast.fahamutech.com"

fun <T> getHttpClient(clazz: Class<T>): T {
    val okHttClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
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

fun messageToApp(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

suspend fun withTryCatch(run: suspend () -> Unit, onError: (message: String) -> Unit) {
    try {
        run()
    } catch (e: Throwable) {
        onError(e.message ?: e.toString())
    }
}

fun duaraLocalToRemoteHash(normalizedContact: String): String {
    return stringToSHA256(stringToSHA256(normalizedContact))
}

fun shareApp(context: Context) {
    val appLink =
        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID} \n\n"
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT, "Pakua app ya Duara kuweza kuchati na" +
                    " marafiki wa rafiki zako " + appLink
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun stringFromDate(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return dateFormat.format(date)
}

fun dateFromString(string: String): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return dateFormat.parse(string) ?: Date()
}