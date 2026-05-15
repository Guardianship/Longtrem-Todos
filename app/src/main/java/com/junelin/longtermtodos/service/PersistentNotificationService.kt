package com.junelin.longtermtodos.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.junelin.longtermtodos.data.repository.SettingsRepository
import com.junelin.longtermtodos.data.repository.TaskRepository
import com.junelin.longtermtodos.reminder.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PersistentNotificationService : Service() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateNotification()
        return START_STICKY
    }

    private fun updateNotification() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            val days = settingsRepository.widgetDisplayDays.first()
            val tasks = taskRepository.getUpcomingTasks(days).first()
            val notification = NotificationHelper.getPersistentNotification(applicationContext, tasks)
            startForeground(1001, notification)
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }
}
