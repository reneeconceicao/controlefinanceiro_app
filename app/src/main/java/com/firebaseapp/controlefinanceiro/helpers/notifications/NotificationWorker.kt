package com.firebaseapp.controlefinanceiro.helpers.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.firebaseapp.controlefinanceiro.MainActivity
import com.firebaseapp.controlefinanceiro.R

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intentOpen = PendingIntent.getActivity(
            appContext,
            999,
            Intent(appContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intentExpense = PendingIntent.getActivity(
            appContext,
            0,
            Intent(appContext, MainActivity::class.java).apply {
                putExtra(ROUTE_EXTRA, 0)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intentIncome = PendingIntent.getActivity(
            appContext,
            1,
            Intent(appContext, MainActivity::class.java).apply {
                putExtra(ROUTE_EXTRA, 1)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        // Get the layouts to use in the custom notification.
        val notificationLayout = RemoteViews(appContext.packageName, R.layout.notification_small).apply {
            setOnClickPendingIntent(R.id.button_open, intentOpen)
            setOnClickPendingIntent(R.id.button_expense, intentExpense)
            setOnClickPendingIntent(R.id.button_income, intentIncome)
        }

        val notificationLayoutExpanded = RemoteViews(appContext.packageName, R.layout.notification_small).apply {
            setOnClickPendingIntent(R.id.button_open, intentOpen)
            setOnClickPendingIntent(R.id.button_expense, intentExpense)
            setOnClickPendingIntent(R.id.button_income, intentIncome)
        }

        // Apply the layouts to the notification.
        val customNotification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setShowWhen(false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCustomBigContentView(notificationLayoutExpanded)
            .build()

        notificationManager.notify(2, customNotification)

        // Indicate whether the work finished successfully with the Result.
        return Result.success()
    }
}