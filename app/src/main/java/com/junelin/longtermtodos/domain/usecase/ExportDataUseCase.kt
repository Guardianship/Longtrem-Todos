package com.junelin.longtermtodos.domain.usecase

import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.data.repository.TaskRepository
import android.content.Context
import com.junelin.longtermtodos.export.DataExportImportManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExportDataUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): String = withContext(Dispatchers.IO) {
        val tasks = taskRepository.getAllActiveTasks().first()
        val categories = categoryRepository.getAllCategoriesSync()
        DataExportImportManager(context).export(tasks, categories)
    }
}
