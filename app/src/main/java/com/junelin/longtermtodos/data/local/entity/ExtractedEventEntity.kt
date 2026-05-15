package com.junelin.longtermtodos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extracted_events")
data class ExtractedEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawText: String,
    val extractedTitle: String,
    val extractedDate: Long?, // LocalDate stored as epochDay, nullable if unclear
    val inferredCategoryId: Long? = null,
    val source: String = EventSource.SMS.name,
    val status: String = ExtractionStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis()
)

enum class EventSource {
    SMS, WECHAT
}

enum class ExtractionStatus {
    PENDING, ACCEPTED, EDITED, IGNORED
}
