package com.junelin.longtermtodos.export

import android.content.Context
import android.net.Uri
import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class BackupData(
    val version: Int = 1,
    val tasks: List<TaskBackup>,
    val categories: List<CategoryBackup>
)

@Serializable
data class TaskBackup(
    val title: String,
    val note: String?,
    val categoryId: Long,
    val dueDate: String,
    val remindBeforeDays: Int,
    val source: String
)

@Serializable
data class CategoryBackup(
    val name: String,
    val icon: String,
    val color: String,
    val sortOrder: Int
)

class DataExportImportManager(private val context: Context) {

    private val json = Json { prettyPrint = true }

    suspend fun export(tasks: List<Task>, categories: List<Category>): String = withContext(Dispatchers.IO) {
        val backup = BackupData(
            tasks = tasks.map {
                TaskBackup(
                    title = it.title,
                    note = it.note,
                    categoryId = it.categoryId,
                    dueDate = it.dueDate.toString(),
                    remindBeforeDays = it.remindBeforeDays,
                    source = it.source.name
                )
            },
            categories = categories.filter { !it.isPreset }.map {
                CategoryBackup(
                    name = it.name,
                    icon = it.icon,
                    color = it.color,
                    sortOrder = it.sortOrder
                )
            }
        )
        json.encodeToString(backup)
    }

    suspend fun import(uri: Uri): BackupData? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val content = stream.bufferedReader().readText()
                json.decodeFromString<BackupData>(content)
            }
        } catch (_: Exception) {
            null
        }
    }
}
