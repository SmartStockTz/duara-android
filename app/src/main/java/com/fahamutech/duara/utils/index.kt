package com.fahamutech.duara.utils

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.Base64
import android.widget.Toast
import com.fahamutech.duara.BuildConfig
import com.fahamutech.duara.models.*
import com.fahamutech.duara.services.getUser
import com.google.gson.Gson
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import com.nimbusds.jose.util.Base64URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets
import java.security.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

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

fun timeAgo(dateString: String): String {
    return try {
        val date: Date = dateFromString(dateString)
        DateUtils.getRelativeTimeSpanString(date.time).toString()
            .replace("ago", "")
            .replace("hours", "h")
            .replace("hour", "h")
            .replace("minutes", "min")
            .replace("minute", "min")
            .replace("yesterday", "Jana")
            .replace("Yesterday", "Jana")
    } catch (_: Throwable) {
        ""
    }
}

suspend fun generateKeyPair(): IdentityModel {
    return withContext(Dispatchers.IO) {
        val ecKeyPair = ECKeyGenerator(Curve.SECP256K1)
            .keyID(UUID.randomUUID().toString())
            .generate()
        val identityModel = IdentityModel()
        identityModel.priv = Gson()
            .fromJson(ecKeyPair.toJSONString(), PrivModel::class.java)
        identityModel.pub = Gson()
            .fromJson(ecKeyPair.toPublicJWK().toJSONString(), PubModel::class.java)
        return@withContext identityModel
    }
}

suspend fun generatePubKey(pubModel: PubModel): PublicKey {
    return withContext(Dispatchers.IO) {
        val xb64url = Base64URL(pubModel.x)
        val yb64url = Base64URL(pubModel.y)
        val ecKey = ECKey.Builder(Curve.SECP256K1, xb64url, yb64url)
            .keyID(pubModel.kid)
            .build()
        return@withContext ecKey.toPublicKey()
    }
}

suspend fun generatePrivKey(privModel: PrivModel): PrivateKey {
    return withContext(Dispatchers.IO) {
        val xb64url = Base64URL(privModel.x)
        val yb64url = Base64URL(privModel.y)
        val db64url = Base64URL(privModel.d)
        val ecKey = ECKey.Builder(Curve.SECP256K1, xb64url, yb64url)
            .d(db64url)
            .keyID(privModel.kid)
            .build()
        return@withContext ecKey.toPrivateKey()
    }
}

suspend fun generateSharedKey(pubModel: PubModel): SecretKey {
    return withContext(Dispatchers.IO) {
        val user = getUser()
        val ka: KeyAgreement = KeyAgreement.getInstance("ECDH")
        ka.init(generatePrivKey(user?.priv!!))
        ka.doPhase(generatePubKey(pubModel), true)
        val sharedKey = ka.generateSecret()
        return@withContext SecretKeySpec(sharedKey, 0, sharedKey.size, "AES")
    }
}

suspend fun encryptMessage(messageLocal: MessageLocal): MessageLocalOutBox {
    return withContext(Dispatchers.IO) {
        val messageByte = Gson().toJson(messageLocal, MessageLocal::class.java)
            .toByteArray(StandardCharsets.UTF_8)
        val sharedKey = generateSharedKey(messageLocal.duara_pub!!)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, sharedKey)
        val encByte = cipher.doFinal(messageByte)

        val enc = Base64.encodeToString(encByte, Base64.DEFAULT)
        val messageOut = MessageLocalOutBox()
        messageOut.to = messageLocal.duara_id!!
        messageOut.from = messageLocal.from
        messageOut.message = enc
        return@withContext messageOut
    }
}

suspend fun decryptMessage(messageRemote: MessageRemote): MessageLocal {
    return withContext(Dispatchers.IO) {
        val sharedKey = generateSharedKey(messageRemote.pub!!)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, sharedKey)
//        val decByte = cipher.doFinal(messageRemote.message).toString()
//        val deString = Base64.encodeToString(decByte, Base64.DEFAULT)

        val decryptMeBytes: ByteArray = Base64.decode(messageRemote.message, Base64.DEFAULT)
        val textBytes = cipher.doFinal(decryptMeBytes)
        val originalText = String(textBytes)
        return@withContext Gson().fromJson(originalText,MessageLocal::class.java)
    }
}








