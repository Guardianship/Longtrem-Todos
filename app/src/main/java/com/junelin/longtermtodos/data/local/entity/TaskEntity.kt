package com.junelin.longtermtodos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId"), Index("dueDate"), Index("isCompleted")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val note: String? = null,
    val categoryId: Long,
    val dueDate: Long, // LocalDate stored as epochDay
    val remindBeforeDays: Int = 3,
    val isCompleted: Boolean = false,
    val source: String = TaskSource.MANUAL.name,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskSource {
    MANUAL, AUTO_SMS, AUTO_WECHAT
}
