package com.junelin.longtermtodos

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class LongTermTodosApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDER,
                getString(R.string.channel_reminder_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_reminder_desc)
            }

            val persistentChannel = NotificationChannel(
                CHANNEL_PERSISTENT,
                getString(R.string.channel_persistent_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_persistent_desc)
            }

            notificationManager.createNotificationChannels(listOf(reminderChannel, persistentChannel))
        }
    }

    companion object {
        const val CHANNEL_REMINDER = "reminder_channel"
        const val CHANNEL_PERSISTENT = "persistent_channel"
    }
}
