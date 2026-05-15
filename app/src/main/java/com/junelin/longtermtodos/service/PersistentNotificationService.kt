package com.junelin.longtermtodos.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.di.AppModule
import com.junelin.longtermtodos.reminder.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PersistentNotificationService : Service() {

    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateNotification()
        return START_STICKY
    }

    private fun updateNotification() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            val repository = AppModule.provideTaskRepository(applicationContext)
            val settings = AppModule.provideSettingsRepository(applicationContext)
            val days = settings.widgetDisplayDays.first()

            val tasks = repository.getUpcomingTasks(days).first()
            val notification = NotificationHelper.getPersistentNotification(applicationContext, tasks)
            startForeground(1001, notification)
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }
}
