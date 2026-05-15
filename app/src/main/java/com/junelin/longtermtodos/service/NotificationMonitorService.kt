package com.junelin.longtermtodos.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.junelin.longtermtodos.data.local.AppDatabase
import com.junelin.longtermtodos.extractor.EventExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationMonitorService : NotificationListenerService() {

    companion object {
        const val WECHAT_PACKAGE = "com.tencent.mm"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != WECHAT_PACKAGE) return

        val title = sbn.notification.extras.getString("android.title") ?: ""
        val text = sbn.notification.extras.getCharSequence("android.text")?.toString() ?: ""

        if (title.isBlank() && text.isBlank()) return

        val database = AppDatabase.getDatabase(applicationContext)
        val extractedEventDao = database.extractedEventDao()

        CoroutineScope(Dispatchers.IO).launch {
            val events = EventExtractor.extractFromNotification(title, text)
            events.forEach { event ->
                extractedEventDao.insert(event.toEntity())
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // No-op
    }
}
