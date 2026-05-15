package com.junelin.longtermtodos.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.receiver.ReminderBroadcastReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class ReminderAlarmManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(task: Task) {
        if (task.isCompleted) return

        val reminderDate = task.dueDate.minusDays(task.remindBeforeDays.toLong())
        val reminderDateTime = reminderDate.atTime(9, 0)
        val triggerTime = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (triggerTime <= System.currentTimeMillis()) return

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("taskId", task.id)
            putExtra("taskTitle", task.title)
            putExtra("dueDate", task.dueDate.toEpochDay())
            putExtra("daysUntil", task.daysUntil)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun cancelReminder(taskId: Long) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun rescheduleAll(tasks: List<Task>) {
        tasks.filter { !it.isCompleted }.forEach { scheduleReminder(it) }
    }
}
