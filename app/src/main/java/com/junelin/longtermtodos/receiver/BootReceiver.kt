package com.junelin.longtermtodos.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.junelin.longtermtodos.di.AppModule
import com.junelin.longtermtodos.reminder.ReminderAlarmManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = AppModule.provideTaskRepository(context)
            val alarmManager = ReminderAlarmManager(context)

            CoroutineScope(Dispatchers.IO).launch {
                repository.getAllActiveTasks().collect { tasks ->
                    alarmManager.rescheduleAll(tasks)
                }
            }
        }
    }
}
