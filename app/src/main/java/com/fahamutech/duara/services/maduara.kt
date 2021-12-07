package com.fahamutech.duara.services

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private fun message(activity: Activity) {
    Toast.makeText(
        activity, "Duara kufanya kazi inabidi uruhusu kuona namba zako, ili uweze ona " +
                "marafiki wa kuchati nao.",
        Toast.LENGTH_LONG
    ).show()
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
            message(activity)
        }.show()
}

fun ensureContactPermission(activity: Activity, granted: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        -> {
            Log.e("DUARA", "CONTACT PERMISSION GRANTED")
            granted()
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_CONTACTS
        )
        -> {
            educationalDialog(activity, granted)
        }
        else -> {
            ActivityCompat.requestPermissions(
                activity,
                mutableListOf(Manifest.permission.READ_CONTACTS).toTypedArray(),
                321
            )
            message(activity)
        }
    }
}
