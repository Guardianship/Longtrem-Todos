package com.junelin.longtermtodos.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.di.AppModule
import com.junelin.longtermtodos.reminder.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("taskId", 0)
        val title = intent.getStringExtra("taskTitle") ?: return
        val dueDate = intent.getLongExtra("dueDate", 0)
        val daysUntil = intent.getLongExtra("daysUntil", 0)

        NotificationHelper.showReminderNotification(
            context = context,
            taskId = taskId,
            title = title,
            dueDate = dueDate,
            daysUntil = daysUntil
        )
    }
}
