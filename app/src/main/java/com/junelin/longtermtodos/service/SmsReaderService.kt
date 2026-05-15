package com.junelin.longtermtodos.service

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import com.junelin.longtermtodos.data.local.dao.ExtractedEventDao
import com.junelin.longtermtodos.data.local.entity.ExtractionStatus
import com.junelin.longtermtodos.extractor.EventExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmsReaderService(
    private val context: Context,
    private val extractedEventDao: ExtractedEventDao
) {

    suspend fun readAndExtract() = withContext(Dispatchers.IO) {
        val cursor = getSmsCursor() ?: return@withContext

        try {
            val bodyIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val count = 0

            while (cursor.moveToNext() && count < 50) {
                val body = cursor.getString(bodyIndex) ?: continue
                val events = EventExtractor.extractFromSms(body)

                events.forEach { event ->
                    // Check if similar event already exists
                    val existing = extractedEventDao.getPendingFlow()
                    // Simplified: just insert
                    extractedEventDao.insert(event.toEntity())
                }
            }
        } finally {
            cursor.close()
        }
    }

    private fun getSmsCursor(): Cursor? {
        return try {
            context.contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                arrayOf(Telephony.Sms._ID, Telephony.Sms.BODY, Telephony.Sms.DATE),
                null,
                null,
                "${Telephony.Sms.DATE} DESC LIMIT 100"
            )
        } catch (_: SecurityException) {
            null
        }
    }
}
