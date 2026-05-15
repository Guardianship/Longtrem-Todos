package com.junelin.longtermtodos.domain.usecase

import android.content.Context
import android.net.Uri
import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.data.repository.TaskRepository
import com.junelin.longtermtodos.export.DataExportImportManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class ImportDataUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val backup = DataExportImportManager(context).import(uri)
                ?: return@withContext Result.failure(Exception("无法解析备份文件"))

            // Import categories first
            val categoryIdMap = mutableMapOf<Long, Long>()
            backup.categories.forEachIndexed { index, catBackup ->
                val category = Category(
                    name = catBackup.name,
                    icon = catBackup.icon,
                    color = catBackup.color,
                    sortOrder = catBackup.sortOrder
                )
                val newId = categoryRepository.insertCategory(category)
                categoryIdMap[index.toLong()] = newId
            }

            // Import tasks
            var count = 0
            backup.tasks.forEach { taskBackup ->
                val categoryId = categoryIdMap[taskBackup.categoryId] ?: 1L
                val task = Task(
                    title = taskBackup.title,
                    note = taskBackup.note,
                    categoryId = categoryId,
                    dueDate = LocalDate.parse(taskBackup.dueDate),
                    remindBeforeDays = taskBackup.remindBeforeDays,
                    source = com.junelin.longtermtodos.data.local.entity.TaskSource.valueOf(taskBackup.source)
                )
                taskRepository.insertTask(task)
                count++
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
