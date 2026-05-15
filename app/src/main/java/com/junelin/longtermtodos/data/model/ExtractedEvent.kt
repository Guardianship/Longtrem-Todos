package com.junelin.longtermtodos.data.model

import com.junelin.longtermtodos.data.local.entity.EventSource
import com.junelin.longtermtodos.data.local.entity.ExtractedEventEntity
import com.junelin.longtermtodos.data.local.entity.ExtractionStatus
import java.time.LocalDate

data class ExtractedEvent(
    val id: Long = 0,
    val rawText: String,
    val extractedTitle: String,
    val extractedDate: LocalDate?,
    val inferredCategoryId: Long? = null,
    val source: EventSource = EventSource.SMS,
    val status: ExtractionStatus = ExtractionStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toEntity(): ExtractedEventEntity = ExtractedEventEntity(
        id = id,
        rawText = rawText,
        extractedTitle = extractedTitle,
        extractedDate = extractedDate?.toEpochDay(),
        inferredCategoryId = inferredCategoryId,
        source = source.name,
        status = status.name,
        createdAt = createdAt
    )

    companion object {
        fun fromEntity(entity: ExtractedEventEntity): ExtractedEvent = ExtractedEvent(
            id = entity.id,
            rawText = entity.rawText,
            extractedTitle = entity.extractedTitle,
            extractedDate = entity.extractedDate?.let { LocalDate.ofEpochDay(it) },
            inferredCategoryId = entity.inferredCategoryId,
            source = EventSource.valueOf(entity.source),
            status = ExtractionStatus.valueOf(entity.status),
            createdAt = entity.createdAt
        )
    }
}
