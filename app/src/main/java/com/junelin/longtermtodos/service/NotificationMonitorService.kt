package com.junelin.longtermtodos.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.junelin.longtermtodos.data.local.dao.ExtractedEventDao
import com.junelin.longtermtodos.extractor.EventExtractor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationMonitorService : NotificationListenerService() {

    @Inject
    lateinit var extractedEventDao: ExtractedEventDao

    companion object {
        const val WECHAT_PACKAGE = "com.tencent.mm"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != WECHAT_PACKAGE) return

        val title = sbn.notification.extras.getString("android.title") ?: ""
        val text = sbn.notification.extras.getCharSequence("android.text")?.toString() ?: ""

        if (title.isBlank() && text.isBlank()) return

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
