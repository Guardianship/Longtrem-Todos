package com.junelin.longtermtodos.domain.usecase

import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(categoryId: Long? = null, query: String? = null): Flow<List<Task>> {
        return when {
            !query.isNullOrBlank() -> taskRepository.searchTasks(query)
            categoryId == null -> taskRepository.getAllActiveTasks()
            else -> taskRepository.getTasksByCategory(categoryId)
        }
    }

    fun getUpcoming(days: Int): Flow<List<Task>> {
        return taskRepository.getUpcomingTasks(days)
    }

    suspend fun getById(taskId: Long): Task? {
        return taskRepository.getTaskByIdSync(taskId)
    }
}
