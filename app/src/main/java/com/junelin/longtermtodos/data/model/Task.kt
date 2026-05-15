package com.junelin.longtermtodos.data.model

import com.junelin.longtermtodos.data.local.entity.TaskEntity
import com.junelin.longtermtodos.data.local.entity.TaskSource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Task(
    val id: Long = 0,
    val title: String,
    val note: String? = null,
    val categoryId: Long,
    val dueDate: LocalDate,
    val remindBeforeDays: Int = 3,
    val isCompleted: Boolean = false,
    val source: TaskSource = TaskSource.MANUAL,
    val createdAt: Long = System.currentTimeMillis()
) {
    val daysUntil: Long
        get() = ChronoUnit.DAYS.between(LocalDate.now(), dueDate)

    val isOverdue: Boolean
        get() = daysUntil < 0 && !isCompleted

    val formattedDueDate: String
        get() = dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val displayDaysLeft: String
        get() = when {
            isCompleted -> "已完成"
            daysUntil == 0L -> "今天到期"
            daysUntil < 0 -> "已逾期 ${-daysUntil} 天"
            else -> "${daysUntil}天后"
        }

    fun toEntity(): TaskEntity = TaskEntity(
        id = id,
        title = title,
        note = note,
        categoryId = categoryId,
        dueDate = dueDate.toEpochDay(),
        remindBeforeDays = remindBeforeDays,
        isCompleted = isCompleted,
        source = source.name,
        createdAt = createdAt
    )

    companion object {
        fun fromEntity(entity: TaskEntity): Task = Task(
            id = entity.id,
            title = entity.title,
            note = entity.note,
            categoryId = entity.categoryId,
            dueDate = LocalDate.ofEpochDay(entity.dueDate),
            remindBeforeDays = entity.remindBeforeDays,
            isCompleted = entity.isCompleted,
            source = TaskSource.valueOf(entity.source),
            createdAt = entity.createdAt
        )
    }
}
