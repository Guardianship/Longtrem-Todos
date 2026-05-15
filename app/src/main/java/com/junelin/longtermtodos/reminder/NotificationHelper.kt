package com.junelin.longtermtodos.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.junelin.longtermtodos.LongTermTodosApp
import com.junelin.longtermtodos.MainActivity
import com.junelin.longtermtodos.R
import java.time.LocalDate

object NotificationHelper {

    fun showReminderNotification(
        context: Context,
        taskId: Long,
        title: String,
        dueDate: Long,
        daysUntil: Long
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("taskId", taskId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dueDateStr = LocalDate.ofEpochDay(dueDate).toString()
        val daysText = when {
            daysUntil == 0L -> "今天"
            daysUntil == 1L -> "明天"
            else -> "${daysUntil}天后"
        }

        val notification = NotificationCompat.Builder(context, LongTermTodosApp.CHANNEL_REMINDER)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle("$title 即将到期")
            .setContentText("将在 $daysText 到期（$dueDateStr）")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(taskId.toInt(), notification)
    }

    fun getPersistentNotification(context: Context, tasks: List<com.junelin.longtermtodos.data.model.Task>): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val content = if (tasks.isEmpty()) {
            "暂无近期待办"
        } else {
            tasks.take(3).joinToString("，") {
                "${it.dueDate.monthValue}月${it.dueDate.dayOfMonth}日 ${it.title}"
            }
        }

        return NotificationCompat.Builder(context, LongTermTodosApp.CHANNEL_PERSISTENT)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle("远期待办")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
    }
}
