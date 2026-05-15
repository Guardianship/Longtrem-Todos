package com.junelin.longtermtodos.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.junelin.longtermtodos.data.repository.TaskRepository
import com.junelin.longtermtodos.reminder.ReminderAlarmManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val alarmManager = ReminderAlarmManager(context)

            CoroutineScope(Dispatchers.IO).launch {
                taskRepository.getAllActiveTasks().collect { tasks ->
                    alarmManager.rescheduleAll(tasks)
                }
            }
        }
    }
}
