package com.fahamutech.duara.services

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import com.fahamutech.duara.models.DuaraLocal
import com.fahamutech.duara.models.DuaraRemote
import com.fahamutech.duara.models.DuaraSync
import com.fahamutech.duara.utils.getHttpClient
import com.fahamutech.duara.utils.messageToApp
import com.fahamutech.duara.utils.stringToSHA256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.await
import retrofit2.http.Body
import retrofit2.http.POST

private interface MaduaraFunctions {
    @POST("/maduara/syncs")
    fun syncs(@Body data: DuaraSync): Call<List<DuaraRemote>>
}

private fun educationalDialog(activity: Activity, granted: () -> Unit) {
    AlertDialog.Builder(activity)
        .setTitle("Ruhusa")
        .setMessage(
            "Duara app, inatumia majina yaliyopo kwenye simu yako" +
                    " ili kukuonyesha watu wakuwasiliana nao, bila kuruhusu app haiwezi fanya " +
                    "kazi. "
        )
        .setPositiveButton("Ruhusu") { d, a ->
            d.dismiss()
            ensureContactPermission(activity, granted)
        }.setNegativeButton("Ghairi") { d, b ->
            d.dismiss()
            messageToApp(
                "Duara kufanya kazi inabidi uruhusu kuona namba zako, ili uweze ona " +
                        "marafiki wa kuchati nao.", activity
            )
        }.show()
}

fun ensureContactPermission(activity: Activity, granted: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED -> {
            Log.e("DUARA", "CONTACT PERMISSION GRANTED")
            granted()
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.READ_CONTACTS
        ) -> {
            educationalDialog(activity, granted)
        }
        else -> {
            ActivityCompat.requestPermissions(
                activity,
                mutableListOf(Manifest.permission.READ_CONTACTS).toTypedArray(),
                321
            )
            messageToApp(
                "Duara kufanya kazi inabidi uruhusu kuona namba zako, ili uweze ona " +
                        "marafiki wa kuchati nao.", activity
            )
        }
    }
}

suspend fun normalisedNumberSignatures(context: Context): List<String> {
    return withContext(Dispatchers.IO) {
        val p1: Array<out String> =
            arrayOf(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
        val contextResolver = context.contentResolver
        val cursor = contextResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            p1, null, null, null
        )
        val numbersN = mutableListOf<String>()
        if (cursor == null) {
            return@withContext numbersN
        }
        try {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val phoneNumberIndexN = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
                    )
                    val numberN = cursor.getStringOrNull(phoneNumberIndexN)
                    if (numberN !== null) {
                        numbersN.add(stringToSHA256(numberN))
//                        numbersN.add(numberN)
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("ERROR FETCH NUMBERS", e.message ?: "")
        } finally {
            cursor.close()
        }
        return@withContext numbersN
    }
}

suspend fun localMaduara(context: Context): List<DuaraLocal> {
    return withContext(Dispatchers.IO) {
        val p1: Array<out String> = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        val contextResolver = context.contentResolver
        val cursor = contextResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            p1, null, null, null
        )
        val numbersN = mutableListOf<DuaraLocal>()
        if (cursor == null) {
            return@withContext numbersN
        }
        try {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val phoneNumberIndexN =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                    val phoneName =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val name = cursor.getStringOrNull(phoneName)
                    val numberN = cursor.getStringOrNull(phoneNumberIndexN)
                    val duaraLocal = DuaraLocal()
                    if (!numberN.isNullOrEmpty()) {
                        duaraLocal.normalizedNumber = numberN
                        if (!name.isNullOrEmpty()) {
                            duaraLocal.name = name
                        } else {
                            duaraLocal.name = numberN
                        }
                        numbersN.add(duaraLocal)
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("ERROR FETCH NUMBERS", e.message ?: "")
        } finally {
            cursor.close()
        }
        return@withContext numbersN
    }
}

suspend fun syncContacts(context: Context): List<DuaraRemote> {
    return withContext(Dispatchers.IO) {
        val user = getUser()
        if (user?.pub != null) {
            val contacts = normalisedNumberSignatures(context)
            val syncSendModel = DuaraSync()
            syncSendModel.maduara = contacts //.subList(0,100)
            syncSendModel.token = user.token
            syncSendModel.nickname = user.nickname
            syncSendModel.picture = user.picture
            syncSendModel.pub = user.pub!!
            syncSendModel.device = getDeviceId(context.contentResolver)
            val maduara =
                getHttpClient(MaduaraFunctions::class.java).syncs(syncSendModel).await()
            saveMaduara(maduara)
            return@withContext mutableListOf()
        } else {
//            throw Throwable(message = "Tumeshindwa jua taarifa zako")
            mutableListOf()
        }
    }
}








