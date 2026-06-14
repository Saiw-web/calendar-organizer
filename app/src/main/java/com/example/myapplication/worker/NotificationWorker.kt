package com.example.myapplication.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.R

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "event_reminders"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        createNotificationChannel()
        showNotification(
            title = inputData.getString("title") ?: "Event Reminder",
            message = inputData.getString("message") ?: "You have an upcoming event"
        )
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Event Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for upcoming calendar events"
            }
            val manager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}
