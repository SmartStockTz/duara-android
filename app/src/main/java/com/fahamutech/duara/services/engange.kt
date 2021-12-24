package com.fahamutech.duara.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.fahamutech.duara.DuaraApp
import com.fahamutech.duara.R
import com.fahamutech.duara.models.Message

private const val NOTIFICATION_CHANNEL = "receive_message"
const val RECEIVE_MESSAGE_NOTIFICATION_ID = 1

private fun getPendingIntent(context: Context, maongezi_id: String): PendingIntent {
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        "https://duaratz.web.app/maongezi/$maongezi_id".toUri(),
        context,
        DuaraApp::class.java,
    )


    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, flags)
        }
    } else {
        TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}

private fun notificationView(context: Context, message: Message): NotificationCompat.Builder {
    val maongeziId = message.sender_pubkey?.x ?: "na"
    val title = message.sender_nickname
    val messageText = message.content
    val pendingIntent = getPendingIntent(context, maongeziId)
    return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(title)
        .setContentText(messageText)
        .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
}

fun showMessageNotification(context: Context, message: Message) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager
    val notificationBuilder = notificationView(context, message)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationBuilder.setChannelId(NOTIFICATION_CHANNEL)
        val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        val name = message.sender_nickname
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL, name, NotificationManager.IMPORTANCE_HIGH
        )
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setSound(ringtoneManager, audioAttributes)
        notificationManager.createNotificationChannel(channel)
    }
    val maongeziId = message.sender_pubkey?.x ?: "na"
    with(NotificationManagerCompat.from(context)) {
        notify(maongeziId, RECEIVE_MESSAGE_NOTIFICATION_ID, notificationBuilder.build())
    }
}

fun playMessageSound(context: Context) {
    val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val r = RingtoneManager.getRingtone(context, notification)
    r.play()
}
