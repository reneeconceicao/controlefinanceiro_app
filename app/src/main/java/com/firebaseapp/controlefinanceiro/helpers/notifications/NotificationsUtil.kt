package com.firebaseapp.controlefinanceiro.helpers.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE

const val CHANNEL_ID = "channelId"

const val ROUTE_EXTRA = "route"

fun createChannel(context: Context) {
    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sincronização",
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager
            .createNotificationChannel(channel)
    }
}
